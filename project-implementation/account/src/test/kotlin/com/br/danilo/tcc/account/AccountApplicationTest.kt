package com.br.danilo.tcc.account

import com.br.danilo.tcc.account.adapter.r2dbc.PostgresTestContainer
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AccountApplicationTest(
    @LocalServerPort private val port: Int,
) : DescribeSpec() {

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgresTestContainer.createContainer().apply {
            org.flywaydb.core.Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations("classpath:db/migration")
                .load()
                .migrate()
        }

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            PostgresTestContainer.configureProperties(postgres, registry)
        }
    }

    private val webClient = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

    init {
        describe("Health check") {
            it("Should be healthy") {
                webClient
                    .get()
                    .uri("/actuator/health")
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBody()
                    .jsonPath("status")
                    .isEqualTo("UP")
            }
        }
    }
}

