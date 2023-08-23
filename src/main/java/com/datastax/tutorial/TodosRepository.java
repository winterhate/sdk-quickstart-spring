package com.datastax.tutorial;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TodosRepository extends CassandraRepository<Todo, UUID> {
    List<Todo> findAllByCompleted(boolean isCompleted, Pageable pageable);
}