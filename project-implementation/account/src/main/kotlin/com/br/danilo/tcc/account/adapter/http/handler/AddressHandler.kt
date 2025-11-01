package com.br.danilo.tcc.account.adapter.http.handler

import com.br.danilo.tcc.account.adapter.http.request.address.UpdateAddressRequest
import com.br.danilo.tcc.account.core.application.address.AddressService
import com.br.danilo.tcc.account.core.application.address.command.CreateAddressCommand
import com.br.danilo.tcc.account.core.application.address.command.UpdateAddressCommand
import com.br.danilo.tcc.account.core.domain.address.AddressId
import com.br.danilo.tcc.account.core.domain.user.UserId
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
class AddressHandler(
    private val service: AddressService,
) {
    suspend fun findAllByUserId(req: ServerRequest): ServerResponse {
        val userId = UserId(req.pathVariable("userId"))
        return ok().bodyValueAndAwait(service.findAllByUserId(userId))
    }

    suspend fun create(req: ServerRequest): ServerResponse {
        val command = req.awaitBody<CreateAddressCommand>()
        val id = service.create(command)
        return created(req.uriBuilder().path("/{id}").build(id)).buildAndAwait()
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val request = req.awaitBody<UpdateAddressRequest>()
        service.update(
            UpdateAddressCommand(
                id = AddressId(req.pathVariable("id")),
                street = request.street,
                number = request.number,
                complement = request.complement,
                neighborhood = request.neighborhood,
                city = request.city,
                state = request.state,
                zipCode = request.zipCode,
            ),
        )

        return noContent().buildAndAwait()
    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val id = AddressId(req.pathVariable("id"))
        service.delete(id)
        return noContent().buildAndAwait()
    }
}
