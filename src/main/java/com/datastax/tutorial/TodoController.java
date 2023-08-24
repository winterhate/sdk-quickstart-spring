package com.datastax.tutorial;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private static final CassandraPageRequest FIRST_TEN = CassandraPageRequest.first(10);

    private final TodosRepository todosRepository;
    private final TodoFactory todoFactory;

    public TodoController(final TodosRepository todosRepository, final TodoFactory todoFactory) {
        this.todosRepository = todosRepository;
        this.todoFactory = todoFactory;
    }

    record TodoApi(@JsonProperty("title") String title) {}

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    static class TodoNotFound extends RuntimeException {
        TodoNotFound(final String message) {
            super(message);
        }
    }

    @GetMapping
    public List<Todo> todos() {
        return todosRepository.findAll(FIRST_TEN).toList();
    }

    @DeleteMapping
    public List<Todo> deleteAll() {
        todosRepository.deleteAll();
        return List.of();
    }

    @GetMapping("/{id}")
    public Todo getById(@PathVariable final UUID id) {
        return todosRepository.findById(id)
                .orElseThrow(() -> new TodoNotFound("""
                    Todo[id=%s] not found""".formatted(id)));
    }

    @PutMapping
    public Todo putTodo(@RequestBody final TodoApi todoApi) {
        final var todo = todoFactory.createTodo(todoApi.title());
        todosRepository.save(todo);
        return todo;
    }

    @DeleteMapping("/{id}")
    public List<Todo> deleteById(@PathVariable final UUID id) {
        todosRepository.deleteById(id);
        return List.of();
    }

    @GetMapping("/open")
    public List<Todo> todosOpen() {
        return todosRepository.findAllByCompleted(false, FIRST_TEN);
    }

    @PostMapping("/{id}/finish")
    public Todo finish(@PathVariable final UUID id) {
        return todosRepository.findById(id).map(todo -> {
                    todo.setCompleted(true);
                    todosRepository.save(todo);
                    return todo;
                })
                .orElseThrow(() -> new TodoNotFound("""
			Todo[id=%s] not found""".formatted(id)));
    }

    @PostMapping("/finishAll")
    public List<Todo> finishAll() {
        return todosRepository.findAll().stream().peek(todo -> {
                    todo.setCompleted(true);
                    todosRepository.save(todo);
                })
                .collect(Collectors.toList());
    }

}
