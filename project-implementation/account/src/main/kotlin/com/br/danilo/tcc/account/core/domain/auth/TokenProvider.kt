package com.br.danilo.tcc.account.core.domain.auth

import com.br.danilo.tcc.account.core.domain.user.Role
import com.br.danilo.tcc.account.core.domain.user.UserId

interface TokenProvider {
    fun generateToken(
        userId: UserId,
        email: String,
        role: Role,
    ): String

    fun validateToken(token: String): Boolean

    fun getClaims(token: String): TokenClaims
}
