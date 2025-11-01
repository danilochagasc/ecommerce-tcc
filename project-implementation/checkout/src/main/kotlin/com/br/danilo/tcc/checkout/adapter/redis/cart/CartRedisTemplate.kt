package com.br.danilo.tcc.checkout.adapter.redis.cart

import com.br.danilo.tcc.checkout.adapter.redis.coupon.CouponRedisTemplate
import com.br.danilo.tcc.checkout.adapter.redis.coupon.toDomain
import com.br.danilo.tcc.checkout.adapter.redis.coupon.toRedisTemplate
import com.br.danilo.tcc.checkout.core.domain.cart.Cart
import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartItem

data class CartRedisTemplate(
    val id: CartId,
    val items: List<CartItem>,
    val coupon: CouponRedisTemplate?,
)

fun CartRedisTemplate.toDomain() =
    Cart(
        id = id,
        items = items,
        coupon = coupon?.toDomain(),
    )

fun Cart.toRedisTemplate() =
    CartRedisTemplate(
        id = id,
        items = items,
        coupon = coupon?.toRedisTemplate(),
    )
