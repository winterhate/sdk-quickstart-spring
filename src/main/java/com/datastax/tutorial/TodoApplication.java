package com.datastax.tutorial;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class TodoApplication {

    private final TodosRepository todosRepository;
    private final TodoFactory todoFactory;

    public TodoApplication(final TodosRepository todosRepository, final TodoFactory todoFactory) {
        this.todosRepository = todosRepository;
        this.todoFactory = todoFactory;
    }

    @PostConstruct
    public void insertTodos() {
        todosRepository.deleteAll();
        todosRepository.save(todoFactory.createTodo("Create Spring Project"));
        todosRepository.save(todoFactory.createTodo("Setup Spring Starter"));
        todosRepository.save(todoFactory.createTodo("Setup Astra Starter"));
    }

}
