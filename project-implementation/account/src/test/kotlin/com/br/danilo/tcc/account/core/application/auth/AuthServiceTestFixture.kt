package com.br.danilo.tcc.account.core.application.auth

import com.br.danilo.tcc.account.core.application.auth.command.LoginCommand

object AuthServiceTestFixture {

    fun loginCommand(
        email: String = "john.doe@example.com",
        password: String = "password123",
    ) = LoginCommand(
        email = email,
        password = password,
    )
}

