package com.ebay.build.persistent.healthcheck.scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ebay.build.persistent.healthcheck.ErrorItem;
import com.ebay.build.persistent.healthcheck.HealthTrackDetailsJDBCTemplate;
import com.ebay.build.persistent.healthcheck.HealthTrackJDBCTemplate;
import com.ebay.build.utils.StringUtils;
import com.ebay.build.validation.model.Level;
import com.ebay.build.validation.model.jaxb.Category;
import com.ebay.build.validation.model.jaxb.Project;
import com.ebay.build.validation.model.jaxb.Results;
import com.ebay.build.validation.util.XmlProcessor;

public class BatchUpdateReportJob implements Job {
	
	private ApplicationContext context = null;
	private final HealthTrackJDBCTemplate modelJDBCTemplate; 
	private final HealthTrackDetailsJDBCTemplate detailsJDBCTemplate;
	private final XmlProcessor jaxbProcessor = new XmlProcessor();
	
	public BatchUpdateReportJob() {
		 context = new ClassPathXmlApplicationContext("healthtrack-sping-jdbc-config.xml");
		 modelJDBCTemplate = (HealthTrackJDBCTemplate) context.getBean("HealthTrackJDBCTemplate");
		 detailsJDBCTemplate = (HealthTrackDetailsJDBCTemplate) context.getBean("HealthTrackDetailsJDBCTemplate");
	}
	
	public void execute(JobExecutionContext context) {
		System.out.println("Executing BatchUpdateReport...");
		Collection<File> resultsFile = loadResultsFile();
		System.out.println("Loaded " + resultsFile);
		for (File resultFile : resultsFile) {
			Results results = jaxbProcessor.unmarshal(resultFile);
			
			int id = -1;
			List<ErrorItem> items = parseResults(results);
        	
        	if (items.size() == 0 || results.getGitURL() == null) {
        	//	if (items.size() == 0 || results.getGitURL() == null || results.getGitBranch() == null) {        		
        		renameDoneFile(resultFile);
				continue;
        	}
        	try {
        		if (!modelJDBCTemplate.isExistIn24H(results.getGitURL(), results.getGitBranch())) {
        			id = modelJDBCTemplate.create(results);
        			detailsJDBCTemplate.create(id, items);
        			System.out.println("inserted session: " + id);
        		}
        	} catch (Exception e) {
        		e.printStackTrace();
        	} finally {
        		renameDoneFile(resultFile);
        	}
		}
	}

	private Collection<File> loadResultsFile() {
		File queueRoot = new File("/raptor.build.service/track/queue");
		System.out.println("queue root: 	" + queueRoot.getAbsolutePath());
		Collection<File> resultFiles = FileUtils.listFiles(queueRoot, 
				FileFilterUtils.suffixFileFilter("xml"), 
				TrueFileFilter.INSTANCE);
		return resultFiles;
	}
	
	private List<ErrorItem> parseResults(Results results) {
    	List<ErrorItem> items = new ArrayList<ErrorItem>();
    	for (Project project : results.getProject()) {
    		for (Category category : project.getCategory()) {
    			for (com.ebay.build.validation.model.jaxb.Error error : category.getErrorList()) {
    				if (Level.WARNING.toString().equals(error.getLevel())) {
    					continue;
    				}
    				ErrorItem item = new ErrorItem();
    				item.setProjectName(project.getName());
    				item.setCatgoryName(category.getName());
    				item.setMessage(error.getMessage());
    				item.setSeverity(error.getLevel());
    				item.setErrorCode(getErrorCode(error.getSolution()));
    				item.setTarget(error.getTarget());
    				items.add(item);
    			}
    		}
    	}
    	return items;
    }
	
	 private String getErrorCode(String solution) {
		 return StringUtils.getFirstFound(solution, "#RaptorProjectHealthFAQs-(ERR-\\d+)", true);
	 }
	 
	 private void renameDoneFile(File file) {
			if (!file.exists()) {
				return;
			}
			File dest = new File(file.getParent(), file.getName() + ".DONE");
			boolean success = file.renameTo(dest);
			if (success) {
				System.out.println("[INFO] Rename Session LOG " + dest);
			} else {
				System.out.println("[WARNING] Failed rename session LOG to " + dest);
			}
		}
}
