package com.br.danilo.tcc.order.adapter.http

import com.br.danilo.tcc.order.adapter.http.handler.OrderHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration(
    private val orderHandler: OrderHandler,
) {
    private companion object {
        const val UUID_REGEX = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
    }

    @Bean
    fun router() =
        coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/order".nest {
                    GET("", orderHandler::findAll)
                    POST("", orderHandler::create)
                    DELETE("/{id}", orderHandler::delete)

                    "/account".nest {
                        GET("/{accountId:$UUID_REGEX}", orderHandler::findAllByAccountId)
                    }
                }
            }
        }
}
