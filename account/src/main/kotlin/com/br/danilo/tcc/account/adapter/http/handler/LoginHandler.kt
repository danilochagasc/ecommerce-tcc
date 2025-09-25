package com.br.danilo.tcc.account.adapter.http.handler

import com.br.danilo.tcc.account.core.application.auth.AuthService
import com.br.danilo.tcc.account.core.application.auth.command.LoginCommand
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class LoginHandler(
    private val service: AuthService,
) {
    suspend fun login(req: ServerRequest): ServerResponse {
        val command = req.awaitBody<LoginCommand>()
        val response = service.login(command)
        return ok().bodyValueAndAwait(response)
    }
}
