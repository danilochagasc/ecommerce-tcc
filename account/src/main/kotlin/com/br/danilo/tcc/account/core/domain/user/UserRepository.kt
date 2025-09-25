package com.br.danilo.tcc.account.core.domain.user

interface UserRepository {
    suspend fun findAll(): List<User>

    suspend fun findById(id: UserId): User?

    suspend fun findByEmail(email: String): User?

    suspend fun create(user: User)

    suspend fun update(user: User)

    suspend fun delete(id: UserId)
}
