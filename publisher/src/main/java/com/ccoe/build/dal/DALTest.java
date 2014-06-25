package com.ccoe.build.dal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.readers.LineProcessor;
import com.ccoe.build.publisher.LoaderProcessor;

public class DALTest {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
//		ApplicationContext context = new ClassPathXmlApplicationContext("sprint-jdbc-config.xml");
//		
//		PluginJDBCTemplate pluginJDBCTemplate = (PluginJDBCTemplate) context.getBean("pluginJDBCTemplate");
//		Plugin plugin = new Plugin();
//		plugin.setGroupId("com.ebay.build");
//		plugin.setArtifactId("maven-build");
//		plugin.setVersion("1.0.0");
//		plugin.setPhaseName("generate_source");
//		plugin.setPluginKey("keyfor plugin");
//		//pluginJDBCTemplate.create(plugin);
//		
//		plugin = pluginJDBCTemplate.getPlugin("keyfor plugin");
//		System.out.println(plugin.getGroupId());
//		
//		
//		RawDataJDBCTemplate rawDataJDBCTemplate = (RawDataJDBCTemplate) context.getBean("rawDataJDBCTemplate");
//		plugin.setId(1);
//		plugin.setDuration(1234L);
//		//rawDataJDBCTemplate.create(plugin, 1, 1);
//		
//		SessionProjectJDBCTemplate spTemplate = (SessionProjectJDBCTemplate) context.getBean("sessionProjectJDBCTemplate");
//		spTemplate.create(1, 1);
		
		LoaderProcessor lp = new LoaderProcessor();
		
		
		BufferedReader br;
		br = new BufferedReader(new FileReader(DALTest.class.getClassLoader().getResource("two_sessions.txt").getFile()));
		
		System.out.println(DALTest.class.getClassLoader().getResource("single_session.txt").getFile());
		
		String sCurrentLine = null;
		StringBuffer sb = new StringBuffer();
		while ((sCurrentLine = br.readLine()) != null) {
			sb.append(sCurrentLine);
			sb.append("\n");
		}
		
		LineProcessor pro = new LineProcessor();
		Session session = pro.process(sb.toString());
		lp.process(session, null);
		
		
//		ProjectJDBCTemplate projectJDBCTemplate = (ProjectJDBCTemplate) context.getBean("projectJDBCTemplate");
//		
//		Project project = new Project();
//		Pool pool = new Pool();
//		pool.setName("this is a test pool");
//		project.setPool(pool);
//		
//		project.setName("EJB Test");
//		project.setGroupId("com.ebay.build");
//		project.setArtifactId("raptor-maven-build");
//		project.setVersion("1.0.0");
//		project.setDuration(12345L);
//		project.setStatus("0");
//		project.setType("WAR");
//		
//		int key = projectJDBCTemplate.create(project);
//		System.out.println("===== KEY:" + key);
		
		
		

//		SessionJDBCTemplate sessionJDBCTemplate = (SessionJDBCTemplate) context.getBean("sessionJDBCTemplate");
//
//		Session session = new Session();
//		session.setUserName("mmao");
//		session.setDuration(1234);
//		session.setEnvironment("RIDE");
//		session.setMavenVersion("3.0.5");
//		session.setStatus("0");
//		session.setStartTime(new Date());
//		Pool pool = new Pool();
//		pool.setName("abc");
//		Machine m = new Machine();
//		m.setName("L-19dkm");
//		pool.setMachine(m);
//		session.setPool(pool);
//		System.out.println("------Records Creation--------");
//		sessionJDBCTemplate.create(session);
//
//		System.out.println("------Listing Multiple Records--------");
//		List<Session> students = sessionJDBCTemplate.listSessions();
//		for (Session record : students) {
//			System.out.print(", Name : " + record.getPool().getName());
//			System.out.println(", Age : " + record.getDuration());
//			System.out.println(", Time : " + record.getStartTime());
//		}
	}
}
