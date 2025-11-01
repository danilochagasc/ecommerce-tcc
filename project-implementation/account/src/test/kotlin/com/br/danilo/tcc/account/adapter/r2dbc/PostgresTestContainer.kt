package com.br.danilo.tcc.account.adapter.r2dbc

import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

object PostgresTestContainer {

    fun createContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"))
            .withDatabaseName("accountdb")
            .withUsername("postgres")
            .withPassword("postgres")
            .apply {
                start()
            }
    }

    fun configureProperties(container: PostgreSQLContainer<*>, registry: DynamicPropertyRegistry) {
        registry.add("spring.r2dbc.url") {
            "r2dbc:postgresql://${container.host}:${container.getMappedPort(5432)}/${container.databaseName}"
        }
        registry.add("spring.r2dbc.username") { container.username }
        registry.add("spring.r2dbc.password") { container.password }
    }
}

