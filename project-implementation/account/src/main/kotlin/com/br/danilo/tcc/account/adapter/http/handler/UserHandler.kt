package com.br.danilo.tcc.account.adapter.http.handler

import com.br.danilo.tcc.account.adapter.http.request.user.ChangePasswordRequest
import com.br.danilo.tcc.account.adapter.http.request.user.UpdateUserRequest
import com.br.danilo.tcc.account.core.application.user.UserService
import com.br.danilo.tcc.account.core.application.user.command.ChangePasswordCommand
import com.br.danilo.tcc.account.core.application.user.command.CreateUserCommand
import com.br.danilo.tcc.account.core.application.user.command.UpdateUserCommand
import com.br.danilo.tcc.account.core.domain.user.UserId
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class UserHandler(
    private val service: UserService,
) {
    suspend fun findAll(req: ServerRequest): ServerResponse = ok().bodyValueAndAwait(service.findAll())

    suspend fun findByLogin(req: ServerRequest): ServerResponse {
        val id = UserId(req.principal().awaitSingle().name)
        return ok().bodyValueAndAwait(service.findById(id))
    }

    suspend fun create(req: ServerRequest): ServerResponse {
        val command = req.awaitBody<CreateUserCommand>()
        val id = service.create(command)
        return created(req.uriBuilder().path("/{id}").build(id)).buildAndAwait()
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val id = UserId(req.pathVariable("id"))
        val request = req.awaitBody<UpdateUserRequest>()
        service.update(
            command =
                UpdateUserCommand(
                    id = id,
                    name = request.name,
                    lastName = request.lastName,
                    cpf = request.cpf,
                    email = request.email,
                    phone = request.phone,
                    birth = request.birth,
                ),
        )

        return noContent().buildAndAwait()
    }

    suspend fun changePassword(req: ServerRequest): ServerResponse {
        val id = UserId(req.pathVariable("id"))
        val request = req.awaitBody<ChangePasswordRequest>()

        service.changePassword(
            command =
                ChangePasswordCommand(
                    id = id,
                    currentPassword = request.currentPassword,
                    newPassword = request.newPassword,
                ),
        )

        return noContent().buildAndAwait()
    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val id = UserId(req.pathVariable("id"))
        service.delete(id)
        return noContent().buildAndAwait()
    }
}
