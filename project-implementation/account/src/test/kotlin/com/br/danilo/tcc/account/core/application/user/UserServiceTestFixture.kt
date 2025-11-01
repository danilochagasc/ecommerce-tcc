package com.br.danilo.tcc.account.core.application.user

import com.br.danilo.tcc.account.core.application.user.command.ChangePasswordCommand
import com.br.danilo.tcc.account.core.application.user.command.CreateUserCommand
import com.br.danilo.tcc.account.core.application.user.command.UpdateUserCommand
import com.br.danilo.tcc.account.core.domain.user.Role
import com.br.danilo.tcc.account.core.domain.user.UserId
import java.time.LocalDate

object UserServiceTestFixture {

    fun createUserCommand(
        name: String = "John",
        lastName: String = "Doe",
        cpf: String = "12345678901",
        email: String = "john.doe@example.com",
        password: String = "password123",
        phone: String = "11999999999",
        birth: LocalDate = LocalDate.of(1990, 1, 1),
        role: String = "USER",
    ) = CreateUserCommand(
        name = name,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        phone = phone,
        birth = birth,
        role = role,
    )

    fun updateUserCommand(
        id: UserId = UserId(),
        name: String = "Jane",
        lastName: String = "Smith",
        cpf: String = "98765432100",
        email: String = "jane.smith@example.com",
        phone: String = "11888888888",
        birth: LocalDate = LocalDate.of(1995, 5, 15),
    ) = UpdateUserCommand(
        id = id,
        name = name,
        lastName = lastName,
        cpf = cpf,
        email = email,
        phone = phone,
        birth = birth,
    )

    fun changePasswordCommand(
        id: UserId = UserId(),
        currentPassword: String = "oldPassword123",
        newPassword: String = "newPassword123",
    ) = ChangePasswordCommand(
        id = id,
        currentPassword = currentPassword,
        newPassword = newPassword,
    )
}

