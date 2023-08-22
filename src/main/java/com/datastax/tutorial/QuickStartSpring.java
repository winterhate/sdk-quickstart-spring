package com.datastax.tutorial;

import com.datastax.astra.sdk.AstraClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@SpringBootApplication
public class QuickStartSpring {

	private final AstraClient astraClient;
	private final TodosRepository todoRepository;
	private final CassandraTemplate cassandraTemplate;


	public QuickStartSpring(final AstraClient astraClient, final TodosRepository todoRepository, final CassandraTemplate cassandraTemplate) {
		this.astraClient = astraClient;
		this.todoRepository = todoRepository;
		this.cassandraTemplate = cassandraTemplate;
	}

	@PostConstruct
	public void insertTodos() {
		todoRepository.deleteAll();
		todoRepository.save(new Todos("Create Spring Project"));
		todoRepository.save(new Todos("Setup Astra Starter"));
		todoRepository.save(new Todos("Setup Spring Starter"));
	}

	@GetMapping("/")
	public String hello() {
		return astraClient.apiDevopsOrganizations().organizationId();
	}

	@GetMapping("/todos")
	public List<Todos> todos() {
		return todoRepository.findAll(CassandraPageRequest.first(10)).toList();
	}

	@DeleteMapping("/todos")
	public List<Todos> deleteAll() {
		todoRepository.deleteAll();
		return List.of();
	}

	@DeleteMapping("/todos/{id}")
	public List<Todos> deleteById(@PathVariable final String id) {
		todoRepository.deleteById(UUID.fromString(id));
		return List.of();
	}

	@PutMapping("/todos")
	public Todos putTodo(@RequestBody final TodoApi todoApi) {
		final var todo = new Todos(todoApi.title());
		todoRepository.save(todo);
		return todo;
	}

	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	static class NotFound extends RuntimeException {
		NotFound(final String message) {
			super(message);
		}
	}

	@PostMapping("/todos/{id}/finish")
	public Todos finish(@PathVariable final UUID id) {
		return todoRepository.findById(id).map(todo -> {
			todo.setCompleted(true);
			todoRepository.save(todo);
			return todo;
		})
		.orElseThrow(() -> new NotFound(String.format("Todo[id=%s] not found", id)));
	}

	@PostMapping("/todos/finish")
	public List<Todos> finishAll() {
		return todoRepository.findAll().stream().peek(todo -> {
			todo.setCompleted(true);
			todoRepository.save(todo);
		})
		.collect(Collectors.toList());
	}

	@GetMapping("/datacenter")
	public String datacenter() {
		return cassandraTemplate
				.getCqlOperations()
				.queryForObject("SELECT data_center FROM system.local", String.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(QuickStartSpring.class, args);
	}
}