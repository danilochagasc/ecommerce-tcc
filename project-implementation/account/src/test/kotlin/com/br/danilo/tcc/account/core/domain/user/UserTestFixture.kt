package com.br.danilo.tcc.account.core.domain.user

import java.time.LocalDate

object UserTestFixture {

    fun user(
        name: String = "John",
        lastName: String = "Doe",
        cpf: String = "12345678901",
        email: String = "john.doe@example.com",
        password: String = "hashedPassword123",
        phone: String = "11999999999",
        birth: LocalDate = LocalDate.of(1990, 1, 1),
        role: Role = Role.USER,
    ) = User.create(
        name = name,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        phone = phone,
        birth = birth,
        role = role,
    )
}

