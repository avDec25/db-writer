package com.migration.db_writer.config;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${db.baseurl}")
    String DB_BASE_URL;

    @Override
    public String getConnectionString() {
        return String.format("couchbase://%s:11210", DB_BASE_URL);
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