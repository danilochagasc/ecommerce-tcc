package com.br.danilo.tcc.account.adapter.http.request.user

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)
