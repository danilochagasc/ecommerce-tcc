package com.br.danilo.tcc.account.core.application.address.query

import com.br.danilo.tcc.account.core.domain.address.Address
import com.br.danilo.tcc.account.core.domain.address.AddressId

data class AddressQuery(
    val id: AddressId,
    val street: String,
    val number: String,
    val complement: String?,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
)

fun Address.toQuery() =
    AddressQuery(
        id = this.id,
        street = this.street,
        number = this.number,
        complement = this.complement,
        neighborhood = this.neighborhood,
        city = this.city,
        state = this.state,
        zipCode = this.zipCode,
    )
