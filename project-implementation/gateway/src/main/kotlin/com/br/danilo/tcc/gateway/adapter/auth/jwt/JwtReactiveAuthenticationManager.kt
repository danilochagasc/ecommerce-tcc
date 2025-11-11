package com.br.danilo.tcc.gateway.adapter.auth.jwt

import com.br.danilo.tcc.gateway.domain.auth.InvalidTokenException
import com.br.danilo.tcc.gateway.domain.auth.TokenValidator
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono

class JwtReactiveAuthenticationManager(
    private val tokenValidator: TokenValidator,
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val token = (authentication?.credentials as? String).orEmpty()
        return Mono
            .justOrEmpty(token)
            .flatMap {
                try {
                    // Valida o token antes de extrair claims
                    if (!tokenValidator.validateToken(it)) {
                        return@flatMap Mono.error<Authentication>(InvalidTokenException())
                    }
                    val claims = tokenValidator.getClaims(it)
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

