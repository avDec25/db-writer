package com.migration.db_writer.service;

import com.migration.db_writer.model.OracleUser;
import com.migration.db_writer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Value("${db.couchbase-writes}")
    boolean COUCHBASE_WRITES;

    @Value("${db.oracle-writes}")
    boolean ORACLE_WRITES;

    @Value("${db.reads}")
    String DB_READS;

    @Autowired
    private CouchbaseUserService couchbaseUserService;

    @Autowired
    private OracleUserService oracleUserService;

    public User saveUser(User user) {
        User oracleSavedUser = null;
        User couchbaseSavedUser = null;
        try {
            if (ORACLE_WRITES) {
                System.out.println("Writing data to Oracle");
                OracleUser oracleUser = oracleUserService.saveUser(user);
                oracleSavedUser = toUser(oracleUser);
                System.out.println("Data saved in Oracle");
            }
        } catch (Exception e) {
            System.out.println("Failed to write data to Oracle");
            e.printStackTrace();
        }

        try {
            if (COUCHBASE_WRITES) {
                System.out.println("Writing data to Couchbase");
                couchbaseSavedUser = couchbaseUserService.saveUser(user);
                System.out.println("Data saved in Couchbase");
            }
        } catch (Exception e) {
            System.out.println("Failed to write data to Couchbase");
        }

        if (ORACLE_WRITES && COUCHBASE_WRITES) { // dual writes enabled
            // verify data saved in both
            if (oracleSavedUser != null && couchbaseSavedUser != null
                    && oracleSavedUser.id.equalsIgnoreCase(couchbaseSavedUser.id)
                    && oracleSavedUser.name.equalsIgnoreCase(couchbaseSavedUser.name)
                    && oracleSavedUser.age == couchbaseSavedUser.age
            ) {
                System.out.println("Dual Writes Verification: Success");
            } else {
                System.out.println("Dual Writes Verification: Failed");
            }
            System.out.println(oracleSavedUser);
            return oracleSavedUser;
        } else if (ORACLE_WRITES) {
            System.out.println(oracleSavedUser);
            return oracleSavedUser;
        } else if (COUCHBASE_WRITES) {
            System.out.println(couchbaseSavedUser);
            return couchbaseSavedUser;
        } else {
            System.out.println("Data Save Failed: No data written to Couchbase or Oracle");
            return new User("", "", 0); // dummy user returned
        }
    }

    public Optional<User> getUserById(String id) {
        try {
            if (DB_READS.equalsIgnoreCase("oracle")) {
                Optional<User> user = oracleUserService.getUserById(id).map(this::toUser);
                System.out.println(user);
                return user;
            }
        } catch (Exception e) {
            System.out.println("Failed to read from Oracle DB");
        }

        Optional<User> user = couchbaseUserService.getUserById(id);
        System.out.println(user);
        return user;
    }

    private User toUser(OracleUser oracleUser) {
        return new User(
                oracleUser.id,
                oracleUser.name,
                oracleUser.age
        );
    }
}