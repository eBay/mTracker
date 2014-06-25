package com.ccoe.build.service.config;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Path("")   
public class BuildConfiguration {

	private ApplicationContext context = null;
	private final ConfigurationJDBCTemplate configJDBCTemplate; 

	public BuildConfiguration() {
		 context = new ClassPathXmlApplicationContext("healthtrack-sping-jdbc-config.xml");
		 configJDBCTemplate = (ConfigurationJDBCTemplate) context.getBean("ConfigurationJDBCTemplate");
	}

    @GET @Path("/config/{appname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Configuration getConfiguration(@PathParam("appname") String appName) {
    	try {
    		Configuration config = configJDBCTemplate.getConfiguration(appName);
    		config.setStatusCode(200);
    		return config;
    	} catch (Exception e) {
    		e.printStackTrace();
    		Configuration config = new Configuration();
    		config.setStatusCode(500);
    		config.setErrorMessage(e.getMessage());
    		return config;
    	}
    }
}
