package com.br.danilo.tcc.gateway.domain.auth

import com.br.danilo.tcc.gateway.domain.user.Role
import com.br.danilo.tcc.gateway.domain.user.UserId

data class TokenClaims(
    val userId: UserId,
    val email: String,
    val role: Role,
    val expiresAtEpochSeconds: Long,
)

