package com.br.danilo.tcc.checkout.core.domain.cart

class CartNotFoundException(
    val cartId: CartId,
) : Exception("Cart with id $cartId not found, please, add at least one item to create a cart")

class RandomCartException(
    val cartId: CartId,
) : Exception("some exception with cart id $cartId")
