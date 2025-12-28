package com.migration.db_writer.repository;

import com.migration.db_writer.model.User;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

public interface UserRepository extends CouchbaseRepository<User, String> {
}
