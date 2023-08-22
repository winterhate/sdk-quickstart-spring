package com.datastax.tutorial;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TodoApi(@JsonProperty("title") String title) {}
