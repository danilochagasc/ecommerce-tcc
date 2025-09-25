package com.br.danilo.tcc.account.adapter.http.request.user

data class CreateUserRequest(
    val name: String,
    val lastName: String,
    val cpf: String,
    val email: String,
    val password: String,
    val phone: String,
    val birth: String,
    val role: String,
)
