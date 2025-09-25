package com.br.danilo.tcc.account.core.application.address.command

import com.br.danilo.tcc.account.core.domain.address.AddressId

data class UpdateAddressCommand(
    val id: AddressId,
    val street: String,
    val number: String,
    val complement: String?,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
)
