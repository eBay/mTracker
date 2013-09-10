package com.ebay.build.alerts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class VelocityParse {

	public VelocityParse(String templateFile, AlertResult ar) {
		
		try {
			Velocity.init();
			VelocityContext context = new VelocityContext();

			context.put("resultlist", ar.getResultlist());

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

			System.out.println("[INFO]: Velocity is ready to generate corresponding html");
			File file = new File("./content.html");
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
