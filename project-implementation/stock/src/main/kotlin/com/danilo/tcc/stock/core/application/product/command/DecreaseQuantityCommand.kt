package com.danilo.tcc.stock.core.application.product.command

import com.danilo.tcc.stock.core.domain.product.ProductId

data class DecreaseQuantityCommand(
    val productId: ProductId,
    val amount: Int,
)
