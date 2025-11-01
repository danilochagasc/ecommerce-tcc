package com.br.danilo.tcc.order.adapter.http

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebFluxConfiguration : WebFluxConfigurer {
    @OptIn(ExperimentalSerializationApi::class)
    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        super.configureHttpMessageCodecs(configurer)
        configurer.defaultCodecs().kotlinSerializationJsonDecoder(
            KotlinSerializationJsonDecoder(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                },
            ),
        )
    }
}
