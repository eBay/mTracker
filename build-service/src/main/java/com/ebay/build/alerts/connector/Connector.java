package com.ebay.build.alerts.connector;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.TimeZone;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
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
			TimeZone timeZone = TimeZone.getDefault();
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			BasicDBObject searchQuery = new BasicDBObject();
			QueryBuilder qb = new QueryBuilder();
			qb.put("Date").greaterThanEquals(startDate).lessThanEquals(endDate);
			searchQuery.putAll(qb.get());
			DBCursor cursor = collection.find(searchQuery);

			while (cursor.hasNext()) {
	
				lastone = cursor.next();
			}
			
			TimeZone.setDefault(timeZone);
		} catch (MongoException e) {
			e.printStackTrace();

		}
		return lastone;
	}
}
