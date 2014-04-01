package com.ebay.build.profiler.publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.ebay.build.dal.PluginJDBCTemplate;
import com.ebay.build.dal.ProjectJDBCTemplate;
import com.ebay.build.dal.RawDataJDBCTemplate;
import com.ebay.build.dal.SessionJDBCTemplate;
import com.ebay.build.dal.SessionProjectJDBCTemplate;
import com.ebay.build.profiler.model.Phase;
import com.ebay.build.profiler.model.Plugin;
import com.ebay.build.profiler.model.Project;
import com.ebay.build.profiler.model.Session;

@Transactional
public class LoaderProcessor {
	//private ApplicationContext context = null;
	
	private PluginJDBCTemplate pluginJDBCTemplate ;
	private ProjectJDBCTemplate projectJDBCTemplate ;
	private RawDataJDBCTemplate rawJDBCTemplate ;
	private SessionJDBCTemplate sessionJDBCTemplate; 
	private SessionProjectJDBCTemplate sessionProjectJDBCTemplate;
	private final Map<String, Plugin> pluginCache = new HashMap<String, Plugin>();
	
	public LoaderProcessor() {
		this("");
	}
	
	public LoaderProcessor(String mavenHome) {
//		if (!"".equals(mavenHome)) {
//			File conf = new File(mavenHome, "conf/spring-jdbc-config.xml");
//			System.out.println("[INFO] Loading raptor tracking db configure file... " + conf);
//			context = new FileSystemXmlApplicationContext(conf.toString());
//		}
	}
	
	public void setSessionJDBCTemplate(SessionJDBCTemplate jdbcTemplate) {
		this.sessionJDBCTemplate = jdbcTemplate;
	}
	
	public void setProjectJDBCTemplate(ProjectJDBCTemplate template) {
		this.projectJDBCTemplate = template;
	}
	
	public void setPluginJDBCTemplate(PluginJDBCTemplate template) {
		this.pluginJDBCTemplate = template;
	}
	
	public void setRawDataJDBCTemplate(RawDataJDBCTemplate template) {
		this.rawJDBCTemplate = template;
	}
	
	public void setSessionProjectJDBCTemplate(SessionProjectJDBCTemplate template) {
		this.sessionProjectJDBCTemplate = template;
	}
	
	public void process(Session session, SessionErrorCollector errorCollector) {
		
		int sessionID = loadSession(session);
		
		System.out.println("**** Transaction Start Session ID " + sessionID + "****");
		if (errorCollector != null) {
			errorCollector.setSessionID(sessionID + "");
		}
		
		if (sessionID < 0) {
			throw new RuntimeException("Session id can not be < 0");	
		}
		
		loadProjects(session, sessionID);
		System.out.println("**** Transaction End Session ID " + sessionID + "****");
	}
	
	protected void loadProjects(Session session, int sessionID) {
		for (Project project : session.getProjects().values()) {
			int projectID = projectJDBCTemplate.create(project, session.getAppName());
			System.out.println("Project ID: " + projectID + " Name: " + project.getName());
			
			linkSessionProject(sessionID, projectID);
			
			List<Plugin> batchInsertPlugins = new ArrayList<Plugin>();
			for (Phase phase : project.getPhases()) {
				for (Plugin plugin : phase.getPlugins()) {
					Plugin dbPlugin = pluginCache.get(plugin.getPluginKey());
					if (dbPlugin == null) {
						dbPlugin = pluginJDBCTemplate.getPlugin(plugin.getPluginKey());
						if (dbPlugin != null) {
							System.out.println("   Plugin " + plugin.getPluginKey() + " loaded from DB. [" + dbPlugin.getId() + "]");
						}
					} else {
						System.out.println("   Plugin " + plugin.getPluginKey() + " loaded from CACHE. [" + dbPlugin.getId() + "]");
					}
					
					if (dbPlugin == null) {
						int pluginDBId = pluginJDBCTemplate.create(plugin);
						System.out.println("    Plugin " + plugin.getPluginKey() + " not exists, created a NEW plugin " + pluginDBId);
						plugin.setId(pluginDBId);
					} else {
						plugin.setId(dbPlugin.getId());
					}
					if (!pluginCache.keySet().contains(plugin.getPluginKey())) {
						pluginCache.put(plugin.getPluginKey(), plugin);
					}
					//rawJDBCTemplate.create(plugin, sessionID, projectID);
				}
				batchInsertPlugins.addAll(phase.getPlugins());
			}
			rawJDBCTemplate.batchInsert(batchInsertPlugins, sessionID, projectID);
		}
	}

	protected void linkSessionProject(int sessionID, int projectID) {
		this.sessionProjectJDBCTemplate.create(sessionID, projectID);
	}

	protected int loadSession(Session session) {
		return this.sessionJDBCTemplate.create(session);
	}
	
	public List<Session> querySessionsWithoutCategory() {
		return sessionJDBCTemplate.getExpSessionWithNullCategory();
	}
	
	public void updateSessionCategory(Session session) {
		sessionJDBCTemplate.updateCategory(session);
	}
	
	public void batchUpdateSessionCategory(List<Session> sessions) {
		sessionJDBCTemplate.batchUpdateCategory(sessions);
	}
}
