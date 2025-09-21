package com.br.danilo.tcc.checkout.adapter.redis.coupon

import com.br.danilo.tcc.checkout.core.domain.coupon.Coupon
import kotlin.time.Instant

data class CouponRedisTemplate(
    val code: String,
    val discountType: String,
    val value: Double,
    val expiresAt: String,
)

fun CouponRedisTemplate.toDomain() =
    Coupon(
        code = code,
        discountType = enumValueOf(discountType),
        value = value,
        expiresAt = Instant.parse(expiresAt)
    )

fun Coupon.toRedisTemplate() =
    CouponRedisTemplate(
        code = code,
        discountType = discountType.name,
        value = value,
        expiresAt = expiresAt.toString()
    )
