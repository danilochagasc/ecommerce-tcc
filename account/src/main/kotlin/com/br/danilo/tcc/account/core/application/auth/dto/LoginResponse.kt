package com.br.danilo.tcc.account.core.application.auth.dto

data class LoginResponse(
    val token: String,
    val expiresAt: Long,
)
