package com.danilo.tcc.stock.core.domain.product

import com.danilo.tcc.stock.core.domain.AggregateId
import com.danilo.tcc.stock.core.domain.category.CategoryId
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.validate

typealias ProductId = AggregateId

data class Product(
    val id: ProductId,
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val categoryId: CategoryId,
) {
    private fun validate() {
        validate(this) {
            validate(Product::name).isNotBlank()
            validate(Product::description).isNotBlank()
            validate(Product::imageUrl).isNotBlank()
            validate(Product::price).isNotNull().isGreaterThanOrEqualTo(0.0)
            validate(Product::quantity).isNotNull().isGreaterThanOrEqualTo(0)
            validate(Product::categoryId).isNotNull()
        }
    }

    companion object {
        fun create(
            name: String,
            description: String,
            imageUrl: String,
            price: Double,
            quantity: Int,
            categoryId: CategoryId,
        ) = Product(
            id = ProductId(),
            name = name,
            description = description,
            imageUrl = imageUrl,
            price = price,
            quantity = quantity,
            categoryId = categoryId,
        ).apply { validate() }
    }

    fun update(
        name: String,
        description: String,
        imageUrl: String,
        price: Double,
        quantity: Int,
        categoryId: CategoryId,
    ) = this
        .copy(
            name = name,
            description = description,
            imageUrl = imageUrl,
            price = price,
            quantity = quantity,
            categoryId = categoryId,
        ).apply { validate() }
}
