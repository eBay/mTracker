package com.ebay.build.profiler.publisher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.ebay.build.dal.PluginCountJDBCTemplate;
import com.ebay.build.profiler.utils.FileUtils;

public class UpdatePluginCountList {
private ApplicationContext context = null;
	
	public UpdatePluginCountList() {
		context = new ClassPathXmlApplicationContext("spring-jdbc-config.xml");
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
		System.out.println("=====" + this.getClass().getResource("/plugin_count_list.txt"));
		listContent = FileUtils.readFile(new File(this.getClass().getResource("/plugin_count_list.txt").getFile()));
		if (listContent == null) {
			return;
		}
		//System.out.println(listContent);
		PluginCountJDBCTemplate pluginCountJDBCTemplate = (PluginCountJDBCTemplate) context.getBean("pluginCountJDBCTemplate");
		List<String> itemInFile = new ArrayList<String>();
		List<String> existedList = pluginCountJDBCTemplate.getIncludedPlugins();
		for (String item : listContent.split("\n")) {
			if (!existedList.contains(item.trim())) {
				System.out.println("Inserted: " + item);
				pluginCountJDBCTemplate.create(item);
			} else {
				System.out.println("=====" + item + " existed");
			}
			itemInFile.add(item);
		}
		
		System.out.println(existedList);
		
		for (String existItem : existedList) {
			if (!itemInFile.contains(existItem)) {
				System.out.println("deleting " + existItem);
				pluginCountJDBCTemplate.delete(existItem);
			} else {
				System.out.println("required " + existItem);
			}
		}
	}
}
