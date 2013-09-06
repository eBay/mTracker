package com.ebay.build.alerts.pfdash;
import java.io.File;

import com.ebay.build.alerts.AlertResult;
import com.ebay.build.alerts.Compare;
import com.ebay.build.alerts.connector.Connector;
import com.mongodb.DB;



public class MainJob {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String location = "sharedmongo1.vip.stratus.dev.ebay.com";

		int port = 27017;

		String dbname = "raptordashboard";
	
		DB db = Connector.connectDB(location, port, dbname);
		
		Compare com = new Compare(new File("./query_config.xml"),db);
		
		AlertResult ar = com.judgeRules();
		
		EmailSender rlb= new EmailSender(ar);
		
		rlb.sendmail();
	}

}
