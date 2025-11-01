package com.br.danilo.tcc.checkout.core.domain.stock

import com.br.danilo.tcc.checkout.core.domain.cart.CartItem
import com.br.danilo.tcc.checkout.core.domain.cart.ProductId

interface StockRepository {
    suspend fun findById(productId: ProductId): CartItem?

    suspend fun decreaseQuantity(
        productId: ProductId,
        amount: Int,
    ): Unit
}
