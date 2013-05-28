package com.ebay.build.profiler.publisher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.ebay.build.dal.PluginCountJDBCTemplate;
import com.ebay.build.profiler.utils.StringUtils;

public class UpdatePluginCountList {
private ApplicationContext context = null;
	
	public UpdatePluginCountList() {
		context = new ClassPathXmlApplicationContext("sprint-jdbc-config.xml");
	}
	
	public UpdatePluginCountList(String mavenHome) {
		File conf = new File(mavenHome, "conf/spring-jdbc-config.xml");
		System.out.println("[INFO] Loading raptor tracking db configure file... " + conf);
		context = new FileSystemXmlApplicationContext(conf.toString());
	}
	
	public static void main(String[] args) {
		UpdatePluginCountList upcl = new UpdatePluginCountList();
		upcl.execute();
	}
	
	protected void execute() {
		String listContent = null;
		try {
			System.out.println("=====" + this.getClass().getResource("/plugin_count_list.txt"));
			listContent = StringUtils.readFile(this.getClass().getResource("/plugin_count_list.txt").getFile(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (listContent == null) {
			return;
		}
		System.out.println(listContent);
		PluginCountJDBCTemplate pluginCountJDBCTemplate = (PluginCountJDBCTemplate) context.getBean("pluginCountJDBCTemplate");
		for (String item : listContent.split("\n")) {
			pluginCountJDBCTemplate.create(item);
		}
	}
}
