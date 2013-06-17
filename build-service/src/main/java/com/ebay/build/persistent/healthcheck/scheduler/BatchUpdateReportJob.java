package com.ebay.build.persistent.healthcheck.scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ebay.build.persistent.healthcheck.ErrorItem;
import com.ebay.build.persistent.healthcheck.HealthTrackDetailsJDBCTemplate;
import com.ebay.build.persistent.healthcheck.HealthTrackJDBCTemplate;
import com.ebay.build.utils.FileUtils;
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
		File[] resultsFile = FileUtils.loadFiles(FileUtils.QUEUE_DIR, FileUtils.XML_EXT);
		if (resultsFile == null) {
			return;
		}
		System.out.println("Loaded " + resultsFile);
		for (File resultFile : resultsFile) {
			Results results = jaxbProcessor.unmarshal(resultFile);
			
			int id = -1;
			List<ErrorItem> items = parseResults(results);
        	
        	if (items.size() == 0 || results.getGitURL() == null) {
        		FileUtils.renameDoneFile(resultFile);
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
        		FileUtils.renameDoneFile(resultFile);
        	}
		}
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
}
