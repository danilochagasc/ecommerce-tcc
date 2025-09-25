package com.br.danilo.tcc.account.core.application.user.query
import com.br.danilo.tcc.account.core.domain.user.Role
import com.br.danilo.tcc.account.core.domain.user.User
import com.br.danilo.tcc.account.core.domain.user.UserId
import java.time.LocalDate

data class UserPrivateQuery(
    val id: UserId,
    val name: String,
    val lastName: String,
    val cpf: String,
    val email: String,
    val password: String,
    val phone: String,
    val birth: LocalDate,
    val role: Role,
)

fun User.toPrivateQuery() =
    UserPrivateQuery(
        id = this.id,
        name = this.name,
        lastName = this.lastName,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        phone = this.phone,
        birth = this.birth,
        role = this.role,
    )
