package com.br.danilo.tcc.checkout.core.application.coupon.command

import kotlin.time.Instant

data class UpdateCouponCommand(
    val code: String,
    val discountType: String,
    val value: Double,
    val expiresAt: Instant,
)