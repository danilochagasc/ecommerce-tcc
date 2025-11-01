package com.br.danilo.tcc.payment.adapter.http.handler

import com.br.danilo.tcc.payment.core.domain.AggregateId
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class PaymentHandler {

    suspend fun forceSuccessPayment(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait("Payment Succeeded")
    }

    suspend fun forceFailurePayment(req: ServerRequest): ServerResponse {
        return badRequest().bodyValueAndAwait("Payment Failed")
    }
}