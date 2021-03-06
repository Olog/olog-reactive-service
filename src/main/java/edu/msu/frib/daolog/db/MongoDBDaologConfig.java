package edu.msu.frib.daolog.db;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "edu.msu.frib.daolog.repository")
public class MongoDBDaologConfig
{
	// Empty class, using mongodb auto-configuration from properties file
}
