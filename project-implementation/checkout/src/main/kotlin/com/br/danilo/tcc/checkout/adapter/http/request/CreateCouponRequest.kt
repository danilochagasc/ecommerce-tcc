package com.br.danilo.tcc.checkout.adapter.http.request

data class CreateCouponRequest(
    val code: String,
    val discountType: String,
    val value: Double,
    val expiresAt: String,
)
