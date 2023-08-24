package com.datastax.tutorial;

import com.datastax.astra.sdk.AstraClient;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class TodoController {

    private final AstraClient astraClient;
    private final TodosRepository todosRepository;
    private final CassandraTemplate cassandraTemplate;
    private final TodoFactory todoFactory;

    public TodoController(final AstraClient astraClient, final TodosRepository todosRepository, final CassandraTemplate cassandraTemplate, final TodoFactory todoFactory) {
        this.astraClient = astraClient;
        this.todosRepository = todosRepository;
        this.cassandraTemplate = cassandraTemplate;
        this.todoFactory = todoFactory;
    }

    @GetMapping("/")
    public String hello() {
        return astraClient.apiDevopsOrganizations().organizationId();
    }

    @GetMapping("/todos")
    public List<Todo> todos() {
        return todosRepository.findAll(CassandraPageRequest.first(10)).toList();
    }

    @GetMapping("/todos/open")
    public List<Todo> todosOpen() {
        return todosRepository.findAllByCompleted(false, CassandraPageRequest.first(10));
    }

    @DeleteMapping("/todos")
    public List<Todo> deleteAll() {
        todosRepository.deleteAll();
        return List.of();
    }

    @DeleteMapping("/todos/{id}")
    public List<Todo> deleteById(@PathVariable final String id) {
        todosRepository.deleteById(UUID.fromString(id));
        return List.of();
    }

    record TodoApi(@JsonProperty("title") String title) {}

    @PutMapping("/todos")
    public Todo putTodo(@RequestBody final TodoApi todoApi) {
        final var todo = todoFactory.createTodo(todoApi.title());
        todosRepository.save(todo);
        return todo;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    static class NotFound extends RuntimeException {
        NotFound(final String message) {
            super(message);
        }
    }

    @PostMapping("/todos/{id}/finish")
    public Todo finish(@PathVariable final UUID id) {
        return todosRepository.findById(id).map(todo -> {
                    todo.setCompleted(true);
                    todosRepository.save(todo);
                    return todo;
                })
                .orElseThrow(() -> new NotFound("""
			Todo[id=%s] not found""".formatted(id)));
    }

    @PostMapping("/todos/finish")
    public List<Todo> finishAll() {
        return todosRepository.findAll().stream().peek(todo -> {
                    todo.setCompleted(true);
                    todosRepository.save(todo);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/datacenter")
    public String datacenter() {
        return cassandraTemplate
                .getCqlOperations()
                .queryForObject("SELECT data_center FROM system.local", String.class);
    }

}