package com.br.danilo.tcc.account.adapter.auth.jwt

import com.br.danilo.tcc.account.core.domain.auth.InvalidTokenException
import com.br.danilo.tcc.account.core.domain.auth.TokenProvider
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono

class JwtReactiveAuthenticationManager(
    private val jwtProvider: TokenProvider,
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication?> {
        val token = (authentication?.credentials as String?) ?: return Mono.empty()
        return Mono.fromCallable {
            if (!jwtProvider.validateToken(token)) throw InvalidTokenException()
            val claims = jwtProvider.getClaims(token)
            UsernamePasswordAuthenticationToken(
                claims.userId,
                token,
                listOf(SimpleGrantedAuthority("ROLE_${claims.role.name}")),
            )
        }
    }
}
