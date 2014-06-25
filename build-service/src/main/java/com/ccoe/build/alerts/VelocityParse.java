package com.ccoe.build.alerts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.ccoe.build.service.BuildServiceScheduler;

public class VelocityParse {

	public VelocityParse(String templateFile, AlertResult ar, Time time, File directory, String dateString) {
		
		try {
			Velocity.init(VelocityParse.class.getResource(
					"/velocity.properties").getFile());
			VelocityContext context = new VelocityContext();
			List<SingleResult> normalResultList = new ArrayList<SingleResult>();
			List<SingleResult> colorResultList = new ArrayList<SingleResult>();
			for (SingleResult result : ar.getResultlist()) {
				if (("#CACACA").equals(result.getColor())) {
					normalResultList.add(result);
				} else {
					colorResultList.add(result);
				}
			}

			context.put("normalResultList", normalResultList);
			context.put("colorResultList", colorResultList);
			context.put("time", time);
			context.put("hostName", BuildServiceScheduler.getHostName());
			context.put("pfdashDataViewService", "http://pfdash/dashsvcs/rest/dataviews/sqls/" + dateString);

			Template template = null;

			try {
				template = Velocity.getTemplate(templateFile);
			} catch (ResourceNotFoundException rnfe) {
				System.out.println("error : cannot find template " + templateFile);
				System.out.println(rnfe.getMessage());
				rnfe.printStackTrace();
			} catch (ParseErrorException pee) {
				System.out.println("Syntax error in template " + templateFile
						+ ":" + pee);
			}

			System.out.println("[INFO]: Velocity is ready to generate corresponding html");
			File file = new File(directory, "PFDash_KPI_Alert.html");
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
