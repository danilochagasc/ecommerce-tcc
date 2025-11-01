package com.danilo.tcc.stock.core.application.product.query

import com.danilo.tcc.stock.core.domain.product.Product

data class ProductQuery(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val categoryId: String,
)

fun Product.toQuery() =
    ProductQuery(
        id = this.id.toString(),
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        price = this.price,
        quantity = this.quantity,
        categoryId = this.categoryId.toString(),
    )
