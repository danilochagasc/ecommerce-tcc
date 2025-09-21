package com.br.danilo.tcc.checkout.core.domain.cart

interface CartRepository {
    suspend fun findById(cartId: CartId): Cart?

    suspend fun createOrUpdate(cart: Cart): Cart

    suspend fun delete(cartId: CartId)
}
