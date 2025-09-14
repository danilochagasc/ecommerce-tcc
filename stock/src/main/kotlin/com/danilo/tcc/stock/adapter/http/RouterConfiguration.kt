package com.danilo.tcc.stock.adapter.http

import com.danilo.tcc.stock.adapter.http.handler.CategoryHandler
import com.danilo.tcc.stock.adapter.http.handler.ProductHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration(
    private val categoryHandler: CategoryHandler,
    private val productHandler: ProductHandler,
) {
    private companion object {
        const val NUMBER_REGEX = "^\\d{6,8}\$"
        const val UUID_REGEX = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
    }

    @Bean
    fun router() =
        coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/category".nest {
                    GET("/{id:$UUID_REGEX}", categoryHandler::findById)
                    GET("", categoryHandler::findAll)
                    POST("", categoryHandler::create)
                    PUT("/{id:$UUID_REGEX}", categoryHandler::update)
                    DELETE("/{id:$UUID_REGEX}", categoryHandler::delete)
                }

                "/product".nest {
                    GET("/{id:$UUID_REGEX}", productHandler::findById)
                    GET("", productHandler::findAll)
                    POST("", productHandler::create)
                    PUT("/{id:$UUID_REGEX}", productHandler::update)
                    DELETE("/{id:$UUID_REGEX}", productHandler::delete)
                }
            }
        }
}
