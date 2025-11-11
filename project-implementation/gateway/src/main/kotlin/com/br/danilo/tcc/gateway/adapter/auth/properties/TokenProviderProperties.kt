package com.br.danilo.tcc.gateway.adapter.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.jwt")
data class TokenProviderProperties(
    val secretKey: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
)

