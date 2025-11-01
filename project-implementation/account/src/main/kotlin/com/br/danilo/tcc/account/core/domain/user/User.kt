package com.br.danilo.tcc.account.core.domain.user

import com.br.danilo.tcc.account.core.domain.AggregateId
import com.br.danilo.tcc.account.core.domain.address.AddressId
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.functions.matches
import org.valiktor.validate
import java.time.LocalDate

typealias UserId = AggregateId

data class User(
    val id: UserId,
    val name: String,
    val lastName: String,
    val cpf: String,
    val email: String,
    val password: String,
    val phone: String,
    val birth: LocalDate,
    val addresses: Set<AddressId>,
    val role: Role,
) {
    init {
        validate()
    }

    private fun validate() {
        validate(this) {
            validate(User::name).isNotBlank()
            validate(User::lastName).isNotBlank()
            validate(User::cpf).isNotBlank().matches(Regex("^[0-9]{11}$"))
            validate(User::email).isNotBlank().isEmail()
            validate(User::password).isNotBlank()
            validate(User::phone).isNotBlank().hasSize(min = 8, max = 15)
            validate(User::birth).isNotNull().isGreaterThanOrEqualTo(LocalDate.of(1900, 1, 1))
            validate(User::addresses).hasSize(min = 0, max = 3)
        }
    }

    companion object {
        fun create(
            name: String,
            lastName: String,
            cpf: String,
            email: String,
            password: String,
            phone: String,
            birth: LocalDate,
            role: Role,
        ) = User(
            id = UserId(),
            name = name,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            phone = phone,
            birth = birth,
            addresses = emptySet(),
            role = role,
        )
    }

    fun update(
        name: String,
        lastName: String,
        cpf: String,
        email: String,
        password: String,
        phone: String,
        birth: LocalDate,
    ) = this.copy(
        name = name,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        phone = phone,
        birth = birth,
    )

    fun changePassword(newPassword: String) = this.copy(password = newPassword)
}
