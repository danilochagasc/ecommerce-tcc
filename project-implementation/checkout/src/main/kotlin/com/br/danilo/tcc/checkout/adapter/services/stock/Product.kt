package com.br.danilo.tcc.checkout.adapter.services.stock

import com.br.danilo.tcc.checkout.core.domain.cart.CartItem
import com.br.danilo.tcc.checkout.core.domain.cart.ProductId

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val categoryId: String,
)

fun Product.toCartItem() =
    CartItem(
        productId = ProductId(id),
        name = name,
        price = price,
        quantity = quantity,
    )
