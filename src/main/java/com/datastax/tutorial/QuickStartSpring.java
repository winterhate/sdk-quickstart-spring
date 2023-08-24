package com.datastax.tutorial;

import com.datastax.astra.sdk.AstraClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@SpringBootApplication
public class QuickStartSpring {

	private static final Logger log = LoggerFactory.getLogger(QuickStartSpring.class);

	private final AstraClient astraClient;
	private final TodosRepository todosRepository;
	private final CassandraTemplate cassandraTemplate;

	public QuickStartSpring(final AstraClient astraClient, final TodosRepository todosRepository, final CassandraTemplate cassandraTemplate) {
		this.astraClient = astraClient;
		this.todosRepository = todosRepository;
		this.cassandraTemplate = cassandraTemplate;
	}

	@PostConstruct
	public void insertTodos() {
		todosRepository.deleteAll();
		todosRepository.save(new Todo("Create Spring Project"));
		todosRepository.save(new Todo("Setup Astra Starter"));
		todosRepository.save(new Todo("Setup Spring Starter"));
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
		final var todo = new Todo(todoApi.title());
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

	private static final Set<String> REQUIRED_ENVIRONMENT_PROPERTIES = Set.of("ASTRA_APPLICATION_TOKEN", "ASTRA_DATABASE_ID", "ASTRA_DATABASE_REGION");

	private static void validateRequiredEnvironmentProperties() {
		REQUIRED_ENVIRONMENT_PROPERTIES.forEach(property -> {
			if (System.getenv(property) == null) {
				log.error("""
						Expecting connection info in environment; define %s""".formatted(String.join(", ", REQUIRED_ENVIRONMENT_PROPERTIES)));
				throw new RuntimeException(String.format("""
						Missing environment property: %s""".formatted(property)));
			}
		});
	}

	public static void main(String[] args) {
		validateRequiredEnvironmentProperties();
		SpringApplication.run(QuickStartSpring.class, args);
	}

}