package com.br.danilo.tcc.payment.adapter.http

import com.br.danilo.tcc.payment.adapter.http.handler.PaymentHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration(
    private val paymentHandler: PaymentHandler,
) {
    private companion object {
        const val NUMBER_REGEX = "\\d+"
        const val UUID_REGEX = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
    }

    @Bean
    fun router() =
        coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "payment".nest {
                    POST("/force-success", paymentHandler::forceSuccessPayment)
                    POST("/force-failure", paymentHandler::forceFailurePayment)
                }
            }
        }
}
