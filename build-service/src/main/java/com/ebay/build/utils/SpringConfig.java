package com.ebay.build.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringConfig {
	private static ApplicationContext context = new ClassPathXmlApplicationContext("udc-sping-jdbc-config.xml");
	public static Object getBean(String beanName){
		return context.getBean(beanName);
	}
}
