package com.ebay.build.alerts.connector;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.ServerAddress;


public class Connector {

	public static int DAYS;
	public static DB connectDB(List<String> location, int port, String dbname) {

		try {
			List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>();
			ServerAddress serverAddress = null;
			for(String loc : location) {
				serverAddress = new ServerAddress(loc, port);
				serverAddressList.add(serverAddress);
			}
			Mongo mongo = new Mongo(serverAddressList);
			DB db = mongo.getDB(dbname);
			return db;

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static DBCollection connectCollection(DB db,String collectionname) {

		try {
			 DBCollection collection = db.getCollection(collectionname);
			 return collection;

		} catch (MongoException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static DBObject getLastRecord(DBCollection collection, Date startDate, Date endDate) {
		DBObject lastone = null;
		try {
			BasicDBObject searchQuery = new BasicDBObject();
			QueryBuilder qb = new QueryBuilder();
			qb.put("Date").greaterThanEquals(startDate).lessThanEquals(endDate);
			searchQuery.putAll(qb.get());
			DBCursor cursor = collection.find(searchQuery);
			
			while (cursor.hasNext()) {
	
				lastone = cursor.next();
			}
			

		} catch (MongoException e) {
			e.printStackTrace();

		}
		return lastone;
	}
	
	
	public static double getMovingAverage(DBCollection collection, String field, Date startDate, Date endDate) {
		double weekSum = 0;
		DBObject record = null;		
		int count = 0;
		try {
			BasicDBObject searchQuery = new BasicDBObject();
			QueryBuilder qb = new QueryBuilder();
			qb.put("Date").greaterThanEquals(startDate).lessThanEquals(endDate);
			searchQuery.putAll(qb.get());		
			DBCursor cursor = collection.find(searchQuery);
			
			while(cursor.hasNext()) {
				record = cursor.next();
				DBObject dbo = (DBObject) record.get("Data");
				Set<String> keySet = dbo.keySet();
				
				for(String keyName : keySet) {
					if (field.equals(keyName)) {
						count++;
						Object keyValue = dbo.get(keyName);
						double keyValueNum = 0;
						if (keyValue instanceof Integer) {
							int intValue = (Integer) keyValue;
							keyValueNum = Double.parseDouble("" + intValue);
						} else if (keyValue instanceof Double) {
							keyValueNum = (Double) keyValue;
						}
						weekSum += keyValueNum;
					}
				}
			}
		} catch (MongoException e) {
			e.printStackTrace();
		}	
		DAYS = count;
		return weekSum * 1.0 / count;
	}
}
