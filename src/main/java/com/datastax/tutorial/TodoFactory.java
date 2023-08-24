package com.datastax.tutorial;

import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class TodoFactory {

    private final Clock clock;

    public TodoFactory(final Clock clock) {
        this.clock = clock;
    }

    public Todo createTodo(String title) {
        return new Todo(title, clock.instant());
    }

}
