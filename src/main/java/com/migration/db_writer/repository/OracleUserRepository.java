package com.migration.db_writer.repository;

import com.migration.db_writer.model.OracleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OracleUserRepository extends JpaRepository<OracleUser, String> {
}
