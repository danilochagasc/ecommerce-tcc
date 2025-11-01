package com.danilo.tcc.stock.core.domain.product

import com.danilo.tcc.stock.core.domain.category.CategoryId

object ProductTestFixture {
    fun product(
        name: String = "Product 1",
        description: String = "Product description",
        imageUrl: String = "https://example.com/image.jpg",
        price: Double = 10.0,
        quantity: Int = 100,
        categoryId: CategoryId = CategoryId(),
    ) = Product.create(
        name = name,
        description = description,
        imageUrl = imageUrl,
        price = price,
        quantity = quantity,
        categoryId = categoryId,
    )

    fun productWithLowQuantity() = product(quantity = 5)

    fun productWithHighQuantity() = product(quantity = 1000)
}
