package com.br.danilo.tcc.checkout.adapter.http.handler

import com.br.danilo.tcc.checkout.core.application.cart.CartService
import com.br.danilo.tcc.checkout.core.application.cart.command.AddCartItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.command.ApplyCouponCommand
import com.br.danilo.tcc.checkout.core.application.cart.command.IncreaseOrDecreaseItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.command.RemoveCartItemCommand
import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartItem
import com.br.danilo.tcc.checkout.core.domain.cart.ProductId
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class CartHandler(
    private val service: CartService,
) {
    suspend fun findById(req: ServerRequest): ServerResponse {
        val cartId = CartId(req.pathVariable("id"))
        return ok().bodyValueAndAwait(service.findById(cartId))
    }

    suspend fun addItem(req: ServerRequest): ServerResponse {
        val command =
            AddCartItemCommand(
                id = CartId(req.pathVariable("id")),
                item = req.awaitBody<CartItem>(),
            )
        service.addItem(command)
        return noContent().buildAndAwait()
    }

    suspend fun increaseItemQuantity(req: ServerRequest): ServerResponse {
        val command =
            IncreaseOrDecreaseItemCommand(
                cartId = CartId(req.pathVariable("cartId")),
                productId = ProductId(req.pathVariable("productId")),
                quantity = req.pathVariable("quantity").toInt(),
            )
        service.increaseItemQuantity(command)
        return noContent().buildAndAwait()
    }

    suspend fun removeItem(req: ServerRequest): ServerResponse {
        val command =
            RemoveCartItemCommand(
                cartId = CartId(req.pathVariable("cartId")),
                productId = ProductId(req.pathVariable("productId")),
            )
        service.removeItem(command)
        return noContent().buildAndAwait()
    }

    suspend fun decreaseItemQuantity(req: ServerRequest): ServerResponse {
        val command =
            IncreaseOrDecreaseItemCommand(
                cartId = CartId(req.pathVariable("cartId")),
                productId = ProductId(req.pathVariable("productId")),
                quantity = req.pathVariable("quantity").toInt(),
            )
        service.decreaseItemQuantity(command)
        return noContent().buildAndAwait()
    }

    suspend fun applyCoupon(req: ServerRequest): ServerResponse {
        val command =
            ApplyCouponCommand(
                id = CartId(req.pathVariable("id")),
                couponCode = req.pathVariable("code"),
            )
        service.applyCoupon(command)
        return noContent().buildAndAwait()
    }

    suspend fun removeCoupon(req: ServerRequest): ServerResponse {
        val cartId = CartId(req.pathVariable("id"))
        service.removeCoupon(cartId)
        return noContent().buildAndAwait()
    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val cartId = CartId(req.pathVariable("id"))
        service.delete(cartId)
        return noContent().buildAndAwait()
    }
}
