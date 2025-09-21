package com.br.danilo.tcc.checkout.core.application.cart.command

import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.ProductId

data class RemoveCartItemCommand(
    val cartId: CartId,
    val productId: ProductId,
)
