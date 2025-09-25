package com.br.danilo.tcc.account.adapter.auth

import com.br.danilo.tcc.account.core.domain.auth.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BcryptPasswordHasher : PasswordHasher {
    companion object {
        private val encoder = BCryptPasswordEncoder()
    }

    override suspend fun hash(raw: String): String =
        withContext(Dispatchers.IO) {
            encoder.encode(raw)
        }

    override suspend fun verify(
        raw: String,
        hashed: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            encoder.matches(raw, hashed)
        }
}
