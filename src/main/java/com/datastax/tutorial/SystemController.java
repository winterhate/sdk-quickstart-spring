package com.datastax.tutorial;

import com.datastax.astra.sdk.AstraClient;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    private final AstraClient astraClient;
    private final CassandraTemplate cassandraTemplate;

    public SystemController(final AstraClient astraClient, final CassandraTemplate cassandraTemplate) {
        this.astraClient = astraClient;
        this.cassandraTemplate = cassandraTemplate;
    }

    @GetMapping("/")
    public String hello() {
        return astraClient.apiDevopsOrganizations().organizationId();
    }

    @GetMapping("/datacenter")
    public String datacenter() {
        return cassandraTemplate
                .getCqlOperations()
                .queryForObject("SELECT data_center FROM system.local", String.class);
    }

}
