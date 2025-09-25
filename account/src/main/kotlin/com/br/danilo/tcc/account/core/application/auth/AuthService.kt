package com.br.danilo.tcc.account.core.application.auth

import com.br.danilo.tcc.account.core.application.auth.command.LoginCommand
import com.br.danilo.tcc.account.core.application.auth.dto.LoginResponse
import com.br.danilo.tcc.account.core.domain.auth.PasswordHasher
import com.br.danilo.tcc.account.core.domain.auth.TokenProvider
import com.br.danilo.tcc.account.core.domain.user.InvalidCredentialsException
import com.br.danilo.tcc.account.core.domain.user.UserNotFoundExceptionByEmail
import com.br.danilo.tcc.account.core.domain.user.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenProvider: TokenProvider,
) {
    suspend fun login(command: LoginCommand): LoginResponse {
        val user = userRepository.findByEmail(command.email) ?: throw UserNotFoundExceptionByEmail(command.email)
        if (!passwordHasher.verify(command.password, user.password)) throw InvalidCredentialsException()
        val token = tokenProvider.generateToken(user.id, user.email, user.role)
        return LoginResponse(
            token = token,
            expiresAt = tokenProvider.getClaims(token).expiresAtEpochSeconds,
        )
    }
}
