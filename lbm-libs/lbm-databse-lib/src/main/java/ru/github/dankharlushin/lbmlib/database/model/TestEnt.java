package ru.github.dankharlushin.lbmlib.database.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_entity")
public class TestEnt {
    private Long id;
    @Column(name = "test_name")
    private String name;

    public void setId(final Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
