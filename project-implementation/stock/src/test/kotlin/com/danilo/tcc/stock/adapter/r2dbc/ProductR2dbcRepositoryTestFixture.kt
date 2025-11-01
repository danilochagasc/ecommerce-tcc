package com.danilo.tcc.stock.adapter.r2dbc

import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.product.Product
import com.danilo.tcc.stock.core.domain.product.ProductId
import java.util.UUID

object ProductR2dbcRepositoryTestFixture {
    private val productId = ProductId(UUID.fromString("17f8b4b5-c55a-4f6d-8427-3f80eb75b801"))
    private val anotherProductId = ProductId(UUID.fromString("a7d9c3e2-4b8f-4a1d-9c5e-3f2a1b4c5d6e"))
    private val categoryId = CategoryId(UUID.fromString("8b1c7a4d-6e58-465e-bc34-b43cf7649b87"))
    private val anotherCategoryId = CategoryId(UUID.fromString("b06b2db2-14b3-4880-856b-af5c5c01c999"))

    fun productId() = productId

    fun anotherProductId() = anotherProductId

    fun categoryId() = categoryId

    fun product() =
        Product(
            id = productId,
            name = "Product 1",
            description = "Product description",
            imageUrl = "https://example.com/image.jpg",
            price = 10.0,
            quantity = 100,
            categoryId = categoryId,
        )

    fun anotherProduct() =
        Product(
            id = anotherProductId,
            name = "Product 2",
            description = "Product description 2",
            imageUrl = "https://example.com/image2.jpg",
            price = 20.0,
            quantity = 200,
            categoryId = categoryId,
        )

    fun productUpdated() =
        Product(
            id = productId,
            name = "Updated Product",
            description = "Updated description",
            imageUrl = "https://example.com/updated-image.jpg",
            price = 15.0,
            quantity = 150,
            categoryId = categoryId,
        )
}
