package com.danilo.tcc.stock.adapter.http.request

import com.danilo.tcc.stock.core.domain.category.CategoryId

data class UpdateProductRequest(
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val categoryId: CategoryId,
)
