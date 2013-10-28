package com.ebay.build.udc.dao;

import com.ebay.build.utils.ServiceConfig;
import com.mongodb.DB;
import java.net.UnknownHostException;


import com.mongodb.Mongo;
import com.mongodb.MongoException;
import org.osgi.framework.ServiceException;

public class UDCMongoDb {

    private static Mongo mongo;
    public static final String SELFSERVICE_DB = ServiceConfig.get("udc.mongodb.name");

    public static void initialize() throws UnknownHostException, MongoException {

        String location = ServiceConfig.get("udc.mongodb.host");
        int port = ServiceConfig.getInt("udc.mongodb.port");

        //mongo = new Mongo( host , port );
        mongo = new Mongo(location,
                port);
    }

    public static Mongo getInstance() throws UnknownHostException, MongoException, ServiceException {
        if (mongo == null) {
            initialize();
        }
        return mongo;
    }

    public static void shutdown() {
        mongo.close();
    }

    public static DB getUsageDataDB() throws UnknownHostException {
        return getInstance().getDB(SELFSERVICE_DB);
    }
}
