package com.ebay.build.alerts.pfdash;
import java.io.File;

import com.ebay.build.alerts.AlertResult;
import com.ebay.build.alerts.Compare;
import com.ebay.build.alerts.Condition;
import com.ebay.build.alerts.connector.Connector;
import com.ebay.build.utils.ServiceConfig;
import com.mongodb.DB;

public class MainJob {

	public static void main(String[] args) {
		String location = ServiceConfig.get("scheduler.alert.pfdash.db.host");
		int port = ServiceConfig.getInt("scheduler.alert.pfdash.db.port");
		String dbname = ServiceConfig.get("scheduler.alert.pfdash.db.name");
	
		DB db = Connector.connectDB(location, port, dbname);
		
		Compare com = new Compare(new File(MainJob.class.getResource(
				"/alert_kpi_threshold.xml").getFile()), db);
		
		Condition condition = new Condition();
		condition.setStartDate(java.sql.Date.valueOf("2013-8-30"));
		condition.setEndDate(java.sql.Date.valueOf("2013-9-2"));
		
		AlertResult ar = com.judgeRules(condition);
		
		EmailSender rlb= new EmailSender(ar);
		
		rlb.sendmail();
	}
}
