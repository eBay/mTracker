package com.ebay.build.cal.processors;

import java.io.File;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.ebay.build.cal.dal.PluginJDBCTemplate;
import com.ebay.build.cal.dal.ProjectJDBCTemplate;
import com.ebay.build.cal.dal.RawDataJDBCTemplate;
import com.ebay.build.cal.dal.SessionJDBCTemplate;
import com.ebay.build.cal.dal.SessionProjectJDBCTemplate;
import com.ebay.build.cal.model.Phase;
import com.ebay.build.cal.model.Plugin;
import com.ebay.build.cal.model.Project;
import com.ebay.build.cal.model.Session;

public class LoaderProcessor {
	private ApplicationContext context = null;
	
	private final SessionJDBCTemplate sessionJDBCTemplate; 
	
	public LoaderProcessor() {
		this("");
	}
	
	public LoaderProcessor(String mavenHome) {
		if ("".equals(mavenHome)) {
			context = new ClassPathXmlApplicationContext("sprint-jdbc-config.xml");
		} else {
			File conf = new File(mavenHome, "conf/spring-jdbc-config.xml");
			System.out.println("[INFO] Loading raptor tracking db configure file... " + conf);
			context = new FileSystemXmlApplicationContext(conf.toString());
		}
		sessionJDBCTemplate = (SessionJDBCTemplate) context.getBean("sessionJDBCTemplate");
	}
	
	public void process(Session session) {
		
		int sessionID = loadSession(session);
		
		if (sessionID < 0) {
			// TODO throw exception, fail the db transaction.
		}
		
		loadProjects(session, sessionID);
		
	}
	
	protected void loadProjects(Session session, int sessionID) {
		PluginJDBCTemplate pluginJDBCTemplate = (PluginJDBCTemplate) context.getBean("pluginJDBCTemplate");
		ProjectJDBCTemplate projectJDBCTemplate = (ProjectJDBCTemplate) context.getBean("projectJDBCTemplate");
		RawDataJDBCTemplate rawJDBCTemplate = (RawDataJDBCTemplate) context.getBean("rawDataJDBCTemplate");
		
		for (Project project : session.getProjects().values()) {
			int projectID = projectJDBCTemplate.create(project);
			
			linkSessionProject(sessionID, projectID);
			
			for (Phase phase : project.getPhases()) {
				for (Plugin plugin : phase.getPlugins()) {
					System.out.println("Project " + project.getName() + " Plugin " + plugin.getPluginKey());
					Plugin dbPlugin = pluginJDBCTemplate.getPlugin(plugin.getPluginKey());
					if (dbPlugin == null) {
						int pluginDBId = pluginJDBCTemplate.create(plugin);
						plugin.setId(pluginDBId);
					} else {
						plugin.setId(dbPlugin.getId());
					}
					rawJDBCTemplate.create(plugin, sessionID, projectID);
				}
			}
		}
	}

	protected void linkSessionProject(int sessionID, int projectID) {
		SessionProjectJDBCTemplate spTemplate = (SessionProjectJDBCTemplate) context.getBean("sessionProjectJDBCTemplate");
		spTemplate.create(sessionID, projectID);
	}

	protected int loadSession(Session session) {
		SessionJDBCTemplate sessionJDBCTemplate = (SessionJDBCTemplate) context.getBean("sessionJDBCTemplate");
		return sessionJDBCTemplate.create(session);
	}
	
	public List<Session> querySessionsWithoutCategory() {
		return sessionJDBCTemplate.getExpSessionWithNullCategory();
	}
	
	public void updateSessionCategory(Session session) {
		sessionJDBCTemplate.updateCategory(session);
	}
}
