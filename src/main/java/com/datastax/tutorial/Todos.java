package com.datastax.tutorial;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table
public class Todos {
    @PrimaryKey
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID uid = UUID.randomUUID();

    private String title;

    private boolean completed = false;

    public Todos() {
    }

    public Todos(String title) {
        this.title = title;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(final UUID uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(final boolean completed) {
        this.completed = completed;
    }
}

