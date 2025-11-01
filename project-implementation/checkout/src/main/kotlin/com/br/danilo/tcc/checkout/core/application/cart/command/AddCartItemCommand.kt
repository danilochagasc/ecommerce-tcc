package com.br.danilo.tcc.checkout.core.application.cart.command

import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartItem

data class AddCartItemCommand(
    val id: CartId,
    val item: CartItem,
)
