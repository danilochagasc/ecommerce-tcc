package com.br.danilo.tcc.account.core.domain.address

import com.br.danilo.tcc.account.core.domain.AggregateId
import com.br.danilo.tcc.account.core.domain.user.UserId
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.validate

typealias AddressId = AggregateId

data class Address(
    val id: AddressId,
    val userId: UserId,
    val street: String,
    val number: String,
    val complement: String?,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
) {
    init {
        validate()
    }

    private fun validate() {
        validate(this) {
            validate(Address::userId).isNotNull()
            validate(Address::street).isNotBlank()
            validate(Address::number).isNotBlank()
            validate(Address::neighborhood).isNotBlank()
            validate(Address::city).isNotBlank()
            validate(Address::state).isNotBlank().hasSize(min = 2, max = 2)
            validate(Address::zipCode).isNotBlank().hasSize(min = 8, max = 8)
        }
    }

    companion object {
        fun create(
            userId: UserId,
            street: String,
            number: String,
            complement: String?,
            neighborhood: String,
            city: String,
            state: String,
            zipCode: String,
        ) = Address(
            id = AddressId(),
            userId = userId,
            street = street,
            number = number,
            complement = complement,
            neighborhood = neighborhood,
            city = city,
            state = state,
            zipCode = zipCode,
        )
    }

    fun update(
        street: String,
        number: String,
        complement: String?,
        neighborhood: String,
        city: String,
        state: String,
        zipCode: String,
    ) = this.copy(
        street = street,
        number = number,
        complement = complement,
        neighborhood = neighborhood,
        city = city,
        state = state,
        zipCode = zipCode,
    )
}
