package com.ebay.build.service.health;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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

@Path("/healthtrack")
public class HealthTrack {
	
	private ApplicationContext context = null;
	private final HealthTrackJDBCTemplate modelJDBCTemplate; 
	private final HealthTrackDetailsJDBCTemplate detailsJDBCTemplate;
	
	public HealthTrack() {
        context = new ClassPathXmlApplicationContext("healthtrack-sping-jdbc-config.xml");
        modelJDBCTemplate = (HealthTrackJDBCTemplate) context.getBean("HealthTrackJDBCTemplate");
        detailsJDBCTemplate = (HealthTrackDetailsJDBCTemplate) context.getBean("HealthTrackDetailsJDBCTemplate");
	}

    @POST
    @Consumes("application/xml")
    @Produces(MediaType.TEXT_PLAIN)
    public String post(Results results) {
    	int id = -1;
        try {
        	List<ErrorItem> items = parseResults(results);
        	
        	if (items.size() == 0) {
        		return "found no critical/major issues.";
        	}
        	if (modelJDBCTemplate.isExistIn24H(results.getGitURL(), results.getGitBranch())) {
        		return "only allow to insert new data after 24 hours";
        	}
        	id = modelJDBCTemplate.create(results);
        	detailsJDBCTemplate.create(id, items);
        } catch (Exception e) {
        	return "Exception raised: " + e.getMessage();
        }
        return "posted:" + id;
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
