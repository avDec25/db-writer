package com.migration.db_writer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@Configuration
@EnableCouchbaseRepositories
public class DBConfig extends AbstractCouchbaseConfiguration {

    @Override
    public String getBucketName() {
        return "user-bucket";
    }

    @Override
    public String getConnectionString() {
        return "couchbase://localhost:11210";
    }

    @Override
    public String getUserName() {
        return "admin";
    }

    @Override
    public String getPassword() {
        return "couchbasepass";
    }
}