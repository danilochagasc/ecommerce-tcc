package com.br.danilo.tcc.checkout.adapter.redis

import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

object RedisTestContainer {
    fun createContainer(): GenericContainer<*> =
        GenericContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .apply {
                start()
            }

    fun configureProperties(
        container: GenericContainer<*>,
        registry: DynamicPropertyRegistry,
    ) {
        registry.add("spring.data.redis.host") { container.host }
        registry.add("spring.data.redis.port") { container.getMappedPort(6379) }
    }
}
