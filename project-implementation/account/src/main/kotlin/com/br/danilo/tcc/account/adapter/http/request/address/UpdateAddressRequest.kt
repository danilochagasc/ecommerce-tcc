package com.br.danilo.tcc.account.adapter.http.request.address

data class UpdateAddressRequest(
    val street: String,
    val number: String,
    val complement: String?,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
)
