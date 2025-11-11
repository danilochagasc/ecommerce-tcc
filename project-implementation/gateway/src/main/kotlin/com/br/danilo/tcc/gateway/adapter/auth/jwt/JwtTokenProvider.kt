package com.br.danilo.tcc.gateway.adapter.auth.jwt

import com.br.danilo.tcc.gateway.adapter.auth.properties.TokenProviderProperties
import com.br.danilo.tcc.gateway.domain.auth.TokenClaims
import com.br.danilo.tcc.gateway.domain.auth.TokenValidator
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

/**
 * Implementação do validador de tokens JWT para o Gateway.
 * Este componente apenas valida e decodifica tokens gerados pelo Account Service.
 * A geração de tokens é responsabilidade exclusiva do Account Service.
 */
@Component
class JwtTokenValidator(
    private val tokenProperties: TokenProviderProperties,
) : TokenValidator {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(tokenProperties.secretKey.toByteArray())

    override fun validateToken(token: String): Boolean =
        try {
            val claims = parseClaims(token)
            !claims.expiration.before(Date())
        } catch (ex: Exception) {
            false
        }

    override fun getClaims(token: String): TokenClaims =
        parseClaims(token).let {
            TokenClaims(
                userId = com.br.danilo.tcc.gateway.domain.user.UserId(it.subject),
                email = it["email"] as String,
                role = com.br.danilo.tcc.gateway.domain.user.Role.valueOf(it["role"] as String),
                expiresAtEpochSeconds = Instant.ofEpochMilli(it.expiration.time).epochSecond,
            )
        }

    private fun parseClaims(token: String): Claims =
        Jwts
            .parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
}

