package com.ebay.build.reliability;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;


public class VelocityParse {

	public VelocityParse(String templateFile, Map<String, ReportInfo> infoList,
			List<ErrorCode> topTenSystemErrors,
			List<ErrorCode> topTenUserErrors) {
		
		String logPath = "./log";
		File dirLog = new File(logPath);
		if(!dirLog.exists()){
			if(!dirLog.mkdir()){
				try {
					throw new Exception("Directory does not exist, fail to create !");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		try {

			Velocity.init(VelocityParse.class.getResource(
					"/velocity.properties").getFile());
			VelocityContext context = new VelocityContext();
			for (Entry<String, ReportInfo> entry : infoList.entrySet()) {
				String key = entry.getKey();
				ReportInfo value = entry.getValue();
				context.put(key, value);
			}
			context.put("topTenSystemErrors", topTenSystemErrors);
			context.put("topTenUserErrors", topTenUserErrors);

			Template template = null;

			try {
				template = Velocity.getTemplate(templateFile);
			} catch (ResourceNotFoundException rnfe) {
				System.out.println("error : cannot find template "
						+ templateFile + rnfe.getMessage());
				rnfe.printStackTrace();
			} catch (ParseErrorException pee) {
				System.out.println("Syntax error in template " + templateFile
						+ ":" + pee);
			}

			
			String path = "./html";
			File dirFile = new File(path);
			if(!dirFile.exists()){
				if(!dirFile.mkdir()){
					throw new Exception("Directory does not exist, fail to create !");
				}
			}
			
			File file = new File(path + "/mail.html");
			if(!file.exists()){
				if(!file.createNewFile()){
					throw new Exception("File does not exist, fail to create !");
				}
			}
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "utf-8"));
			if (template != null)
				template.merge(context, writer);
			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
			}

}
