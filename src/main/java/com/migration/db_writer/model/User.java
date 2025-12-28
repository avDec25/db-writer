package com.migration.db_writer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class User {
    @Id
    public String id;
    public String name;
    public int age;
}
