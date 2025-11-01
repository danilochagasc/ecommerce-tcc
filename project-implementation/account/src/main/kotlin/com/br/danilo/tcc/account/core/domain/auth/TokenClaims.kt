package com.br.danilo.tcc.account.core.domain.auth

import com.br.danilo.tcc.account.core.domain.user.Role
import com.br.danilo.tcc.account.core.domain.user.UserId

data class TokenClaims(
    val userId: UserId,
    val email: String,
    val role: Role,
    val expiresAtEpochSeconds: Long,
)
