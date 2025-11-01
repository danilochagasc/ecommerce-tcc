package com.br.danilo.tcc.account.core.application.user.query

import com.br.danilo.tcc.account.core.domain.user.User
import com.br.danilo.tcc.account.core.domain.user.UserId
import java.time.LocalDate

data class UserPublicQuery(
    val id: UserId,
    val name: String,
    val lastName: String,
    val cpf: String,
    val email: String,
    val phone: String,
    val birth: LocalDate,
)

fun User.toPublicQuery() =
    UserPublicQuery(
        id = this.id,
        name = this.name,
        lastName = this.lastName,
        cpf = this.cpf,
        email = this.email,
        phone = this.phone,
        birth = this.birth,
    )
