package com.ccoe.build.alerts.devx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccoe.build.alerts.AlertEmail;
import com.ccoe.build.alerts.Condition;
import com.ccoe.build.alerts.connector.Connector;
import com.ccoe.build.alerts.connector.XMLConnector;
import com.ccoe.build.core.utils.DateUtils;
import com.ccoe.build.core.utils.FileUtils;
import com.ccoe.build.service.BuildServiceScheduler;
import com.ccoe.build.utils.ServiceConfig;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DevxReportJob implements Job {
	
	private static final String HTML_CACHE_FILE = "DevX_Metrics_Alert.html";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("DevXReport.....");
		
		DevxReportJob job = new DevxReportJob();
		File file = null;
		try {
			file = new File(DevxReportJob.class.getResource("/").toURI());
			job.run(file);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		run(BuildServiceScheduler.contextPath);	
	}
	
	private DBObject getDataObjFromDB(DB db, String collectionName, Condition current) {
		DBCollection dbc = Connector.connectCollection(db, collectionName);
		DBObject totaldbo = Connector.getLastRecord(dbc, current.getStartDate(), current.getEndDate());
		System.out.println("collectionName: " + collectionName + " start " + current.getStartDate() + " end " + current.getEndDate());
		
		if (totaldbo != null) {
			return (DBObject) totaldbo.get("Data");
		} else {
			return null;
		}
	}
	
	private double getValueFromDataObj(DBObject dbo, String fieldName) {
		Set<String> keyset = dbo.keySet();

		for (String keyname : keyset) {
			if (fieldName.equals(keyname)) {
				Object keyvalue = dbo.get(keyname);
				if (keyvalue instanceof Integer) {
					int k1 = (Integer) keyvalue;
					return Double.parseDouble("" + k1);
				} else if (keyvalue instanceof Double) {
					return (Double) keyvalue;
				} 
			}
		}
		return -1;
	}
	
	private void velocityParse(String templateFile, ValueRetriever retriever, File directory) {
		try {
			Velocity.init(DevxReportJob.class.getResource("/velocity.properties").getFile());
			VelocityContext context = new VelocityContext();

			context.put("valueRetriever", retriever);
			context.put("hostName", BuildServiceScheduler.getHostName());

			Template template = Velocity.getTemplate(templateFile);

			System.out.println("[INFO]: Velocity is ready to generate corresponding html");
			File file = new File(directory, HTML_CACHE_FILE);
			if (!file.exists()) {
				if (!file.createNewFile()) {
					System.out.println("File does not exist, fail to create !");
				}
			}

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			if (template != null) {
				template.merge(context, writer);
			}
			writer.flush();
			writer.close();
			System.out.println("[INFO]: Complete generating corresponding html");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run(File targetDir) {
		Date date = new Date();
		Date currentStartTime = DateUtils.getUTCOneDayBack(date);
		Date currentEndTime = DateUtils.getUTCMidnightZero(date);
		Condition current = new Condition();		
		current.setStartDate(currentStartTime);
		current.setEndDate(currentEndTime);
	
		try {
			
			DB db = Connector.connectDB(Arrays.asList(ServiceConfig.get("pfdash.db.host").split(";")), 
					ServiceConfig.getInt("pfdash.db.port"), 
					ServiceConfig.get("pfdash.db.name"));
			db.slaveOk();
			
			File parentDirectory = new File(DevxReportJob.class.getResource("/").toURI());
			File xmlFile = new File(parentDirectory, "devx_metrics_collections.xml");
			if (!xmlFile.exists()) {
				throw new FileNotFoundException();
			}
			
			Collections collections = XMLConnector.unmarshal(xmlFile, Collections.class);
			
			for (Collection collection : collections.getCollections()) {
				DBObject dbo = getDataObjFromDB(db, collection.getName(), current);
				double total = 0;
				for (Field field : collection.getFields()) {
					if (dbo != null) {
						double valueFromDB = this.getValueFromDataObj(dbo, field.getName());
						if (collection.getName().contains("SRP")) {
							total += valueFromDB;
						}
						field.setValue(Math.round(valueFromDB*100.0)/100.0);
					} else {
						if (collection.getName().contains("SRP")) {
							total = -1;
						}
						field.setValue(-1);
					}
				}
				if (collection.getName().contains("SRP")) {
					Field f = new Field();
					f.setName("TotalBuildTime");
					f.setValue(Math.round(total*100.0)/100.0);
					collection.getFields().add(f);
				}
			}
			
			System.out.println("Generating result page under folder:" + targetDir);
			velocityParse("devx_metrics.vm", new ValueRetriever(collections), targetDir);
			
			String htmlContent = FileUtils.readFile(new File(targetDir, HTML_CACHE_FILE));
			
			AlertEmail emailer = new AlertEmail("devx");
			emailer.sendMail(targetDir, htmlContent, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
