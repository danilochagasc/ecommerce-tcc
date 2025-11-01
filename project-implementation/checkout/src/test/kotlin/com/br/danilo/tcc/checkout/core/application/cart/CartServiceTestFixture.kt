package com.br.danilo.tcc.checkout.core.application.cart

import com.br.danilo.tcc.checkout.core.application.cart.command.AddCartItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.command.ApplyCouponCommand
import com.br.danilo.tcc.checkout.core.application.cart.command.IncreaseOrDecreaseItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.command.RemoveCartItemCommand
import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartItem
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartItem
import com.br.danilo.tcc.checkout.core.domain.cart.ProductId

object CartServiceTestFixture {
    fun addCartItemCommand(
        cartId: CartId = cartId(),
        item: CartItem = cartItem(),
    ) = AddCartItemCommand(
        id = cartId,
        item = item,
    )

    fun increaseItemCommand(
        cartId: CartId = cartId(),
        productId: ProductId = ProductId(),
        quantity: Int = 1,
    ) = IncreaseOrDecreaseItemCommand(
        cartId = cartId,
        productId = productId,
        quantity = quantity,
    )

    fun decreaseItemCommand(
        cartId: CartId = cartId(),
        productId: ProductId = ProductId(),
        quantity: Int = 1,
    ) = IncreaseOrDecreaseItemCommand(
        cartId = cartId,
        productId = productId,
        quantity = quantity,
    )

    fun removeCartItemCommand(
        cartId: CartId = cartId(),
        productId: ProductId = ProductId(),
    ) = RemoveCartItemCommand(
        cartId = cartId,
        productId = productId,
    )

    fun applyCouponCommand(
        cartId: CartId = cartId(),
        couponCode: String = "PROMO10",
    ) = ApplyCouponCommand(
        id = cartId,
        couponCode = couponCode,
    )
}
