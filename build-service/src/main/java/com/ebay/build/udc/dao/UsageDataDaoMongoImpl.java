/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebay.build.udc.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import com.ebay.build.udc.UsageDataInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Phase2: switch to mongoDb.
 * @author wecai
 */
public class UsageDataDaoMongoImpl implements IUsageDataDao {

    @Override
    public int[] insertUsageData(List<UsageDataInfo> infos) throws DaoException {
        DBCollection col = getUsageCollection();

        List<DBObject> dbObjects = new ArrayList<DBObject>();
        for (UsageDataInfo usageDataInfo : infos) {
            dbObjects.add(toDbObject(usageDataInfo));
        }

       col.insert(dbObjects);

        return new int[0];
    }

//    public void update(UsageDataInfo request) throws DaoException {
//        // check whether the request already exists
//        UsageDataInfo old = get(request.getId());
//
//        if (old != null) {
//            try {
//                DBCollection collection = getUsageCollection();
//                collection.update(toDbObject(old), toDbObject(request));
//            } catch (UnknownHostException e) {
//                throw new DaoException(e);
//            } catch (MongoException e) {
//                throw new DaoException(e);
//            }
//        }
//    }
//
//    public void delete(UsageDataInfo request) throws DaoException {
//        // check whether the request already exists
//        UsageDataInfo old = get(request.getId());
//
//        try {
//            DBCollection collection = getUsageCollection();
//            collection.remove(toDbObject(old));
//        } catch (UnknownHostException e) {
//            throw new DaoException(e);
//        } catch (MongoException e) {
//            throw new DaoException(e);
//        }
//    }
//
//    public UsageDataInfo get(int id) throws DaoException {
//        UsageDataInfo request = null;
//
//        BasicDBObject dbobject = new BasicDBObject();
//        dbobject.append("id", id);
//
//        try {
//            DBCollection collection = getUsageCollection();
//            DBCursor cursor = collection.find(dbobject);
//            int count = 0;
//            while (cursor.hasNext()) {
//                cursor.next();
//                // System.out.println( n.toString());
//                count++;
//            }
//
//            if (count > 1) {
//                // error condition
//                throw new DaoException(
//                        "Has more than one entry for this request: id= " + id);
//            }
//
//            if (count == 1) {
//                DBObject data = collection.findOne(dbobject);
//                request = convertJsonToJava(data);
//            }
//        } catch (Exception e) {
//            throw new DaoException(e);
//        }
//        return request;
//    }

    private DBCollection getUsageCollection() {

        try {
            DB db = UDCMongoDb.getUsageDataDB();
            return db.getCollection("usagedata");
        } catch (UnknownHostException ex) {
            Logger.getLogger(UsageDataDaoMongoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private BasicDBObject toDbObject(UsageDataInfo t) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(t, BasicDBObject.class);
    }

	@Override
	public List<UsageDataInfo> queryUsageData(UsageDataInfo data)
			throws DaoException {
		// TODO Auto-generated method stub
		return null;
	}
}
