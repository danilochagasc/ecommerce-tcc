package com.danilo.tcc.stock.core.application.product.command

import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.product.ProductId

data class UpdateProductCommand(
    val id: ProductId,
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val categoryId: CategoryId,
)
