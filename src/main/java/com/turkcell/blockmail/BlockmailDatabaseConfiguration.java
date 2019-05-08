package com.turkcell.blockmail;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.connection.ConnectionId;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.event.ConnectionAddedEvent;
import com.mongodb.event.ConnectionPoolListener;
import com.mongodb.event.ConnectionPoolListenerAdapter;
import com.mongodb.event.ConnectionPoolOpenedEvent;

@Configuration
@EnableMongoRepositories(basePackages = "com.turkcell.blockmail")
public class BlockmailDatabaseConfiguration extends AbstractMongoConfiguration {
	


	@Override
	public MongoClient mongoClient() {
		
	
		MongoClientOptions options = new MongoClientOptions.Builder()
				.applicationName("blockMail")
				.connectionsPerHost(100)
				.build();
		MongoClient mongoClient = new MongoClient(Collections.singletonList(new ServerAddress("127.0.0.1", 27017)), 
									MongoCredential.createScramSha1Credential("admin", "admin", "141991".toCharArray()), options);
 
		return mongoClient;
	}

	@Override
	protected String getDatabaseName() {
		return "productDB";
	}
	
	@Bean
	public MongoDbFactory mongoDbFactory(){
		return new SimpleMongoDbFactory(mongoClient(), getDatabaseName() );
	}
	
	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoDbFactory());
	}

}
