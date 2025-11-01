package com.br.danilo.tcc.checkout.adapter.http.handler

import com.br.danilo.tcc.checkout.core.application.cart.query.CartQuery
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartId
import com.br.danilo.tcc.checkout.core.domain.cart.ProductId

object CartHandlerTestFixture {
    fun cartQuery() =
        CartQuery(
            id = cartId().toString(),
            items =
                listOf(
                    CartQuery.CartItemQuery(
                        productId = ProductId().toString(),
                        name = "Product 1",
                        price = 10.0,
                        quantity = 2,
                    ),
                ),
            coupon = null,
            total = 20.0,
        )

    fun cartQueryWithCoupon() =
        CartQuery(
            id = cartId().toString(),
            items =
                listOf(
                    CartQuery.CartItemQuery(
                        productId = ProductId().toString(),
                        name = "Product 1",
                        price = 10.0,
                        quantity = 2,
                    ),
                ),
            coupon = "PROMO10",
            total = 18.0,
        )
}
