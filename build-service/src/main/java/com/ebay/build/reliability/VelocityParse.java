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
			Map<String, ReportInfo> ServerinfoList,
			List<ErrorCode> topTenSystemErrors,
			List<ErrorCode> topTenUserErrors,
			List<ErrorCode> serverSystemErrors,
			List<ErrorCode> serverUserErrors, String htmlFile,
			File directory) {

		try {
			Velocity.init(VelocityParse.class.getResource(
					"/velocity.properties").getFile());
			VelocityContext context = new VelocityContext();
			if (infoList != null) {
				for (Entry<String, ReportInfo> entry : infoList.entrySet()) {
					String key = entry.getKey();
					ReportInfo value = entry.getValue();
					context.put(key, value);
				}
			}

			if (ServerinfoList != null) {
				for (Entry<String, ReportInfo> entry : ServerinfoList
						.entrySet()) {
					String key = entry.getKey();
					ReportInfo value = entry.getValue();
					context.put(key, value);
				}
			}
			context.put("topTenSystemErrors", topTenSystemErrors);
			context.put("topTenUserErrors", topTenUserErrors);
			if (serverSystemErrors != null && serverUserErrors != null) {
				context.put("ServerSystemErrors", serverSystemErrors);
				context.put("ServerUserErrors", serverUserErrors);
			}

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
			File dirFile = new File(directory, "html");
			if (!dirFile.exists()) {				
				if (!dirFile.mkdirs()) {
					System.out.println("Directory does not exist, fail to create !");
				}
			}

			System.out.println("[INFO]: Velocity is ready to generate corresponding html");
			File file = new File(dirFile, htmlFile);
			if (!file.exists()) {
				if (!file.createNewFile()) {
					System.out.println("File does not exist, fail to create !");
				}
			}

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "utf-8"));
			if (template != null)
				template.merge(context, writer);
			writer.flush();
			writer.close();
			System.out.println("[INFO]: Complete generating corresponding html");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
