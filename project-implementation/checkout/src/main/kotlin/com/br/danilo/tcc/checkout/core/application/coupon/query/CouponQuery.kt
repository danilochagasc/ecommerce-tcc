package com.br.danilo.tcc.checkout.core.application.coupon.query
import com.br.danilo.tcc.checkout.core.domain.coupon.Coupon

data class CouponQuery(
    val code: String,
    val discountType: String,
    val value: Double,
    val expiresAt: String,
)

fun Coupon.toQuery() =
    CouponQuery(
        code = this.code,
        discountType = this.discountType.name,
        value = this.value,
        expiresAt = this.expiresAt.toString(),
    )
