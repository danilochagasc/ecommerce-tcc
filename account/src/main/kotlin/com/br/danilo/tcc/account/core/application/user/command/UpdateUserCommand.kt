package com.br.danilo.tcc.account.core.application.user.command

import com.br.danilo.tcc.account.core.domain.user.UserId
import java.time.LocalDate

data class UpdateUserCommand(
    val id: UserId,
    val name: String,
    val lastName: String,
    val cpf: String,
    val email: String,
    val phone: String,
    val birth: LocalDate,
)
