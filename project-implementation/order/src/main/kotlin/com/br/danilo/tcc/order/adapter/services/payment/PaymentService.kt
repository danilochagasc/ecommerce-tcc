package com.br.danilo.tcc.order.adapter.services.payment

import com.br.danilo.tcc.order.core.application.payment.PaymentService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class PaymentService(
    @Qualifier("paymentWebClient")
    private val paymentWebClient: WebClient,
) : PaymentService {
    override suspend fun authorize() =
        try {
            paymentWebClient
                .post()
                .uri("/force-success")
                .retrieve()
                .awaitBody<String>()
        } catch (ex: Exception) {
            throw ex
        }
}
