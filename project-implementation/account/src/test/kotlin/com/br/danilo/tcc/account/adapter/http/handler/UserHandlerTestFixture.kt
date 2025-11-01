package com.br.danilo.tcc.account.adapter.http.handler

import com.br.danilo.tcc.account.core.application.user.query.UserPublicQuery
import com.br.danilo.tcc.account.core.domain.user.UserId
import java.time.LocalDate

object UserHandlerTestFixture {

    fun userPublicQuery() =
        UserPublicQuery(
            id = UserId(),
            name = "John",
            lastName = "Doe",
            cpf = "12345678901",
            email = "john.doe@example.com",
            phone = "11999999999",
            birth = LocalDate.of(1990, 1, 1),
        )

    fun anotherUserPublicQuery() =
        UserPublicQuery(
            id = UserId(),
            name = "Jane",
            lastName = "Smith",
            cpf = "98765432100",
            email = "jane.smith@example.com",
            phone = "11888888888",
            birth = LocalDate.of(1995, 5, 15),
        )
}

