package com.datastax.tutorial;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface TodosRepository extends CassandraRepository<Todos, UUID> {}