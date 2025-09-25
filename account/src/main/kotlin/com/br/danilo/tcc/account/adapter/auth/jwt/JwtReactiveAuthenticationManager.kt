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
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val token = (authentication?.credentials as? String).orEmpty()
        return Mono
            .justOrEmpty(token)
            .flatMap {
                try {
                    val claims = jwtProvider.getClaims(it)
                    Mono.just(
                        UsernamePasswordAuthenticationToken(
                            claims.userId,
                            it,
                            listOf(SimpleGrantedAuthority("ROLE_${claims.role.name}")),
                        ),
                    )
                } catch (ex: Exception) {
                    Mono.error<Authentication>(InvalidTokenException())
                }
            }
    }
}
