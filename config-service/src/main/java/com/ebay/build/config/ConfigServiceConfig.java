/**
 * 
 */
package com.ebay.build.config;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

/**
 * @author bishen
 * 
 */
@Configuration
@PropertySource("classpath:db.properties")
@ComponentScan
@EnableMongoRepositories
class ConfigServiceConfig {

	private static final String PROP_DB_HOSTS = "db.hosts";
	private static final String PROP_DB_PORT = "db.port";
	private static final String PROP_APP_DB_NAME = "app.db.name";

	@Autowired
	private Environment env;

	@Bean
	public Mongo mongo() throws UnknownHostException {
		String[] hosts = env.getProperty(PROP_DB_HOSTS).split(",");
		int port = Integer.parseInt(env.getProperty(PROP_DB_PORT));
		ArrayList<ServerAddress> seeds = new ArrayList<ServerAddress>();
		for (String loc : hosts) {
			seeds.add(new ServerAddress(loc, port));
		}
		return new MongoClient(seeds);
	}

	@Bean
	public MongoTemplate mongoTemplate(Mongo mongo) {
		MongoTemplate result = new MongoTemplate(mongo,
				env.getProperty(PROP_APP_DB_NAME));
		result.getDb().setReadPreference(ReadPreference.secondaryPreferred());
		return result;
	}

}
