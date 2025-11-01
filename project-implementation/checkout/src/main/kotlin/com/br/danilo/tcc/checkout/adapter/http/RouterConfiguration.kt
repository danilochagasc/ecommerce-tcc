package com.br.danilo.tcc.checkout.adapter.http

import com.br.danilo.tcc.checkout.adapter.http.handler.CartHandler
import com.br.danilo.tcc.checkout.adapter.http.handler.CouponHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration(
    private val couponHandler: CouponHandler,
    private val cartHandler: CartHandler,
) {
    private companion object {
        const val NUMBER_REGEX = "\\d+"
        const val UUID_REGEX = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
    }

    @Bean
    fun router() =
        coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/coupon".nest {
                    GET("/{code}", couponHandler::findByCode)
                    GET("", couponHandler::findAll)
                    POST("", couponHandler::create)
                    PUT("/{code}", couponHandler::update)
                    DELETE("/{code}", couponHandler::delete)
                }

                "/cart".nest {
                    GET("/{id:$UUID_REGEX}", cartHandler::findById)
                    POST("/{id:$UUID_REGEX}", cartHandler::addItem)
                    PUT(
                        "/{cartId:$UUID_REGEX}/item/{productId:$UUID_REGEX}/increase/{quantity:$NUMBER_REGEX}",
                        cartHandler::increaseItemQuantity,
                    )
                    DELETE("/{cartId:$UUID_REGEX}/item/{productId:$UUID_REGEX}", cartHandler::removeItem)
                    PUT(
                        "/{cartId:$UUID_REGEX}/item/{productId:$UUID_REGEX}/decrease/{quantity:$NUMBER_REGEX}",
                        cartHandler::decreaseItemQuantity,
                    )
                    PUT("/{id:$UUID_REGEX}/coupon/{code}", cartHandler::applyCoupon)
                    DELETE("/{id:$UUID_REGEX}/coupon", cartHandler::removeCoupon)
                    DELETE("/{id:$UUID_REGEX}", cartHandler::delete)
                }
            }
        }
}
