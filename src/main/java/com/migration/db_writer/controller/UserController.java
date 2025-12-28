package com.migration.db_writer.controller;

import com.migration.db_writer.model.User;
import com.migration.db_writer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // WRITE
    @PostMapping("users")
    public User createUser(@RequestBody User user) {
        return service.saveUser(user);
    }

    // READ
    @GetMapping("users/{id}")
    public Optional<User> getUser(@PathVariable String id) {
        return service.getUserById(id);
    }
}
