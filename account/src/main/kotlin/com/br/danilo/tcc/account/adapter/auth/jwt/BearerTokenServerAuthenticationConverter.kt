package com.br.danilo.tcc.account.adapter.auth.jwt

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class JwtAuthenticationToken(
    val token: String,
) : AbstractAuthenticationToken(null) {
    private var principalVal: Any? = null

    override fun getCredentials(): Any = token

    override fun getPrincipal(): Any? = principalVal

    fun setPrincipal(p: Any) {
        principalVal = p
    }
}

class BearerTokenServerAuthenticationConverter : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<org.springframework.security.core.Authentication> {
        val header = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.empty()
        if (!header.startsWith("Bearer ")) return Mono.empty()
        val token = header.substringAfter("Bearer ").trim()
        return Mono.just(JwtAuthenticationToken(token))
    }
}
