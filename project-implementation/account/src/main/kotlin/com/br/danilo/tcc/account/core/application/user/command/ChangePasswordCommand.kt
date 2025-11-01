package com.br.danilo.tcc.account.core.application.user.command

import com.br.danilo.tcc.account.core.domain.user.UserId

data class ChangePasswordCommand(
    val id: UserId,
    val currentPassword: String,
    val newPassword: String,
)
