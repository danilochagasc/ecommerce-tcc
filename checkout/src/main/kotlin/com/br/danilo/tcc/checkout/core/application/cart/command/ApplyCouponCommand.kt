package com.br.danilo.tcc.checkout.core.application.cart.command

import com.br.danilo.tcc.checkout.core.domain.cart.CartId

data class ApplyCouponCommand(
    val id: CartId,
    val couponCode: String,
)
