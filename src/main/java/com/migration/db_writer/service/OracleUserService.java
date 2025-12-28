package com.migration.db_writer.service;

import com.migration.db_writer.model.OracleUser;
import com.migration.db_writer.model.User;
import com.migration.db_writer.repository.OracleUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OracleUserService {

    @Autowired
    private OracleUserRepository repository;

    public OracleUserService(OracleUserRepository repository) {
        this.repository = repository;
    }

    // WRITE to OracleDB
    public OracleUser saveUser(User user) {
        OracleUser oracleUser = new OracleUser();
        oracleUser.id = user.id;
        oracleUser.name = user.name;
        oracleUser.age = user.age;
        return repository.save(oracleUser);
    }

    // READ from OracleDB
    public Optional<OracleUser> getUserById(String id) {
        return repository.findById(id);
    }
}