package com.br.danilo.tcc.checkout.core.application.cart.query

import com.br.danilo.tcc.checkout.core.domain.cart.Cart
import com.br.danilo.tcc.checkout.core.domain.cart.CartItem

data class CartQuery(
    val id: String,
    val items: List<CartItemQuery>,
    val coupon: String?,
    val total: Double,
) {
    data class CartItemQuery(
        val productId: String,
        val name: String,
        val price: Double,
        val quantity: Int,
    )
}

fun List<CartItem>.toQuery() =
    this.map {
        CartQuery.CartItemQuery(
            productId = it.productId.toString(),
            name = it.name,
            price = it.price,
            quantity = it.quantity,
        )
    }

fun Cart.toQuery() =
    CartQuery(
        id = this.id.toString(),
        items = this.items.toQuery(),
        coupon = this.coupon?.code,
        total = this.total(),
    )
