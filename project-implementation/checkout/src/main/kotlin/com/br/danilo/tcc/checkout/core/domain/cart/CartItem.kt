package com.br.danilo.tcc.checkout.core.domain.cart

data class CartItem(
    val productId: ProductId,
    val name: String,
    val price: Double,
    val quantity: Int,
)
