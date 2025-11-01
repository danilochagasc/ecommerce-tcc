package com.br.danilo.tcc.account.adapter.r2dbc

import com.br.danilo.tcc.account.core.domain.user.Role
import com.br.danilo.tcc.account.core.domain.user.User
import com.br.danilo.tcc.account.core.domain.user.UserId
import java.time.LocalDate
import java.util.UUID

object UserR2dbcRepositoryTestFixture {

    private val userId = UserId(UUID.fromString("8b1c7a4d-6e58-465e-bc34-b43cf7649b87"))
    private val anotherUserId = UserId(UUID.fromString("b06b2db2-14b3-4880-856b-af5c5c01c999"))

    fun userId() = userId
    fun anotherUserId() = anotherUserId

    fun user() =
        User(
            id = userId,
            name = "John",
            lastName = "Doe",
            cpf = "12345678901",
            email = "john.doe@example.com",
            password = "hashedPassword",
            phone = "11999999999",
            birth = LocalDate.of(1990, 1, 1),
            addresses = emptySet(),
            role = Role.USER,
        )

    fun anotherUser() =
        User(
            id = anotherUserId,
            name = "Jane",
            lastName = "Smith",
            cpf = "98765432100",
            email = "jane.smith@example.com",
            password = "hashedPassword",
            phone = "11888888888",
            birth = LocalDate.of(1995, 5, 15),
            addresses = emptySet(),
            role = Role.USER,
        )
}

