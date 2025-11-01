package com.br.danilo.tcc.account.core.application.user.command

import java.time.LocalDate

data class CreateUserCommand(
    val name: String,
    val lastName: String,
    val cpf: String,
    val email: String,
    val password: String,
    val phone: String,
    val birth: LocalDate,
    val role: String,
)
