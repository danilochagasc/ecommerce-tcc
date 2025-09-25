package com.br.danilo.tcc.account.adapter.http.request.user

import java.time.LocalDate

data class UpdateUserRequest(
    val name: String,
    val lastName: String,
    val cpf: String,
    val email: String,
    val phone: String,
    val birth: LocalDate,
)
