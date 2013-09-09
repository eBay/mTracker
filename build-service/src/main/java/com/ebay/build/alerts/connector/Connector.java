package com.ebay.build.alerts.connector;
import java.net.UnknownHostException;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
//import java.sql.Date;


public class Connector {

	/**
	 * @param args
	 */

	public static DB connectDB(String location, int port, String dbname) {

		try {
			Mongo mongo = new Mongo(location, port);
			DB db = mongo.getDB(dbname);
			return db;

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static DBCollection connectCollection(DB db,String collectionname) {

		try {
			 DBCollection collection = db.getCollection(collectionname);
			 return collection;

		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static DBObject getLastRecord(DBCollection collection, Date startDate, Date endDate) {

		DBObject lastone = null;
		try {
			BasicDBObject searchQuery = new BasicDBObject();

			searchQuery.put("Date", new BasicDBObject("$gte", startDate).append("$lte", endDate));

			DBCursor cursor = collection.find(searchQuery);

			while (cursor.hasNext()) {
	
				lastone = cursor.next();
			}

		} catch (MongoException e) {
			e.printStackTrace();

		}
		return lastone;

	}
}
