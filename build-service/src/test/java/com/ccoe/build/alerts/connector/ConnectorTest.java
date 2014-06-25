package com.ccoe.build.alerts.connector;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.ccoe.build.alerts.connector.Connector;
import com.ccoe.build.core.utils.DateUtils;
import com.ccoe.build.utils.ServiceConfig;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class ConnectorTest {
	
	@Test
	public void TestGetLastRecord() {
		
		String[] location = ServiceConfig.get("pfdash.db.host").split(";");
		List<String> locationList = Arrays.asList(location);
		int port = ServiceConfig.getInt("pfdash.db.port");
		String dbname = ServiceConfig.get("pfdash.db.name");
		DB db = Connector.connectDB(locationList, port, dbname);
		db.slaveOk();
		Date date = new Date();
		Date startTime = DateUtils.getUTCOneDayBack(date);
		Date endTime = DateUtils.getUTCMidnightZero(date); 
		
		DBCollection dbc = Connector.connectCollection(db, "RIDEWorkspaceSetupData");
		DBObject dbo = Connector.getLastRecord(dbc, startTime, endTime);		
		System.out.println(dbo);
	}
}
