package com.br.danilo.tcc.checkout.core.domain.cart

import com.br.danilo.tcc.checkout.core.domain.coupon.Coupon
import java.util.UUID

data class Cart(
    val ownerId: UUID,
    val items: List<CartItem>,
    val coupon: Coupon?,
    val total: Double
){
    data class CartItem(
        val productId: UUID,
        val name: String,
        val description: String,
        val imageUrl: String,
        val price: Double,
        val quantity: Int,
    )

}