package com.br.danilo.tcc.account.core.domain.auth

interface PasswordHasher {
    suspend fun hash(raw: String): String

    suspend fun verify(
        raw: String,
        hashed: String,
    ): Boolean
}
