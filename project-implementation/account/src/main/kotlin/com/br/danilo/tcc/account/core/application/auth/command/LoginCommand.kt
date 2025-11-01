package com.br.danilo.tcc.account.core.application.auth.command

data class LoginCommand(
    val email: String,
    val password: String,
)
