package com.datastax.tutorial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import java.util.Set;

public class QuickStartSpring {

	private static final Logger log = LoggerFactory.getLogger(QuickStartSpring.class);

	private static final Set<String> REQUIRED_ENVIRONMENT_PROPERTIES = Set.of("ASTRA_APPLICATION_TOKEN",
			"ASTRA_DATABASE_ID", "ASTRA_DATABASE_REGION");

	public static void main(String[] args) {
		validateRequiredEnvironmentProperties();
		SpringApplication.run(TodoApplication.class, args);
	}

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

}