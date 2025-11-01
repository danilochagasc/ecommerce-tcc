package com.br.danilo.tcc.account.adapter.auth.jwt

import com.br.danilo.tcc.account.adapter.auth.properties.TokenProviderProperties
import com.br.danilo.tcc.account.core.domain.auth.TokenClaims
import com.br.danilo.tcc.account.core.domain.auth.TokenProvider
import com.br.danilo.tcc.account.core.domain.user.Role
import com.br.danilo.tcc.account.core.domain.user.UserId
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val tokenProperties: TokenProviderProperties,
) : TokenProvider {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(tokenProperties.secretKey.toByteArray())

    override fun generateToken(
        userId: UserId,
        email: String,
        role: Role,
    ): String {
        val now = Date()
        val expiry = Date(now.time + tokenProperties.accessTokenExpiration)

        return Jwts
            .builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .claim("email", email)
            .claim("role", role.name)
            .signWith(secretKey)
            .compact()
    }

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
                userId = UserId(it.subject),
                email = it["email"] as String,
                role = Role.valueOf(it["role"] as String),
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
