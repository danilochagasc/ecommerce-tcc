package com.br.danilo.tcc.checkout.adapter.services

import io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS
import io.netty.handler.timeout.ReadTimeoutHandler
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider.builder
import java.time.Duration.ofSeconds
import java.util.concurrent.TimeUnit

object ConnectionFactory {
    @OptIn(ExperimentalSerializationApi::class)
    fun webClient(
        properties: ConnectionProperties,
        providerName: String = "default-connection-provider",
    ): WebClient {
        val json =
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }

        val provider =
            builder(providerName)
                .maxConnections(500)
                .metrics(true)
                .maxIdleTime(ofSeconds(20))
                .maxLifeTime(ofSeconds(60))
                .pendingAcquireTimeout(ofSeconds(60))
                .evictInBackground(ofSeconds(120))
                .build()

        return WebClient
            .builder()
            .baseUrl(properties.baseUrl)
            .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .codecs { codecConfigurer ->
                codecConfigurer.defaultCodecs().apply {
                    kotlinSerializationJsonEncoder(KotlinSerializationJsonEncoder(json))
                    kotlinSerializationJsonDecoder(KotlinSerializationJsonDecoder(json))
                }
            }.clientConnector(
                ReactorClientHttpConnector(
                    HttpClient
                        .create(provider)
                        .option(CONNECT_TIMEOUT_MILLIS, properties.connTimeout.toMillis().toInt())
                        .doOnConnected { connection ->
                            val timeout = properties.connReadTimeout.toMillis()

                            connection.addHandlerLast(ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                        },
                ),
            ).build()
    }
}
