package com.br.danilo.tcc.account.core.application.address.command

import com.br.danilo.tcc.account.core.domain.user.UserId

data class CreateAddressCommand(
    val userId: UserId,
    val street: String,
    val number: String,
    val complement: String?,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
)
