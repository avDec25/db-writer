package com.migration.db_writer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USERS")
@Getter
@Setter
public class OracleUser {

    @Id
    @Column(name = "ID", length = 64)
    public String id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public Integer age;
}