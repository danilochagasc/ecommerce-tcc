package com.br.danilo.tcc.account.adapter.http

import com.br.danilo.tcc.account.adapter.http.handler.AddressHandler
import com.br.danilo.tcc.account.adapter.http.handler.LoginHandler
import com.br.danilo.tcc.account.adapter.http.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration(
    private val addressHandler: AddressHandler,
    private val userHandler: UserHandler,
    private val loginHandler: LoginHandler,
) {
    private companion object {
        const val UUID_REGEX = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
    }

    @Bean
    fun router() =
        coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/address".nest {
                    GET("{userId:$UUID_REGEX}", addressHandler::findAllByUserId)
                    POST("", addressHandler::create)
                    PUT("/{id:$UUID_REGEX}", addressHandler::update)
                    DELETE("/{id:$UUID_REGEX}", addressHandler::delete)
                }

                "/auth".nest {
                    POST("/login", loginHandler::login)
                }

                "/user".nest {
                    GET("", userHandler::findAll)
                    GET("/findByLogin", userHandler::findByLogin)
                    POST("/register", userHandler::create)
                    PUT("/{id:$UUID_REGEX}", userHandler::update)
                    PUT("/{id:$UUID_REGEX}/password", userHandler::changePassword)
                    DELETE("/{id:$UUID_REGEX}", userHandler::delete)
                }
            }
        }
}
