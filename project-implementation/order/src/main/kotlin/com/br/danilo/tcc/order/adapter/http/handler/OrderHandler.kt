package com.br.danilo.tcc.order.adapter.http.handler

import com.br.danilo.tcc.order.core.application.order.OrderService
import com.br.danilo.tcc.order.core.application.order.command.CreateOrderCommand
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.order.OrderId
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
class OrderHandler(
    private val service: OrderService,
) {
    suspend fun findAll(req: ServerRequest): ServerResponse = ok().bodyValueAndAwait(service.findAll())

    suspend fun findAllByAccountId(req: ServerRequest): ServerResponse {
        val accountId = AccountId(req.pathVariable("accountId"))
        return ok().bodyValueAndAwait(service.findAllByAccountId(accountId))
    }

    suspend fun create(req: ServerRequest): ServerResponse {
        val command = req.awaitBody<CreateOrderCommand>()
        val id = service.create(command)
        return created(req.uriBuilder().path("/{id}").build(id)).buildAndAwait()
    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val orderId = OrderId(req.pathVariable("id"))
        service.delete(orderId)
        return noContent().buildAndAwait()
    }
}
