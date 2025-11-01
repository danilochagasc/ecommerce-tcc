package com.br.danilo.tcc.checkout

import com.br.danilo.tcc.checkout.adapter.redis.RedisTestContainer
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class CheckoutApplicationTests(
    private val env: Environment,
    private val webClient: WebTestClient,
) : DescribeSpec() {
    companion object {
        @Container
        @JvmStatic
        val redis: GenericContainer<*> = RedisTestContainer.createContainer()

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            RedisTestContainer.configureProperties(redis, registry)
        }
    }

    init {
        describe("Test health check") {
            it("Should be healthy") {
                val managementPort =
                    env.getProperty("local.management.port")
                        ?: env.getProperty("local.server.port")
                        ?: throw IllegalStateException("Could not determine server port")

                webClient
                    .get()
                    .uri("http://localhost:$managementPort/actuator/health")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("status")
                    .isEqualTo("UP")
            }
        }
    }
}
