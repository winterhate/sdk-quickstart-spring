package com.datastax.tutorial;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@SuppressWarnings("unused")
@Table("todos")
public class Todo {

    @PrimaryKey
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID uid = UUID.randomUUID();

    private String title;
    private Instant created;

    @Indexed
    private boolean completed = false;

    public Todo() {
    }

    public Todo(String title, Instant created) {
        this.title = title;
        this.created = created;
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

    public Instant getCreated() {
        return created;
    }

    public void setCreated(final Instant created) {
        this.created = created;
    }
}
