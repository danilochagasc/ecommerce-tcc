package com.br.danilo.tcc.checkout.adapter.http.request

data class UpdateCouponRequest(
    val discountType: String,
    val value: Double,
    val expiresAt: String,
)
