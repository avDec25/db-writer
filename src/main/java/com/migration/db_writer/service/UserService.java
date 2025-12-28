package com.migration.db_writer.service;

import com.migration.db_writer.model.User;
import com.migration.db_writer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // WRITE to Couchbase
    public User saveUser(User user) {
        return repository.save(user);
    }

    // READ from Couchbase
    public Optional<User> getUserById(String id) {
        return repository.findById(id);
    }
}