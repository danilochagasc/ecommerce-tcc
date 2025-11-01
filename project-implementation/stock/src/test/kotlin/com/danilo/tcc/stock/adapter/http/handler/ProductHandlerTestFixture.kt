package com.danilo.tcc.stock.adapter.http.handler

import com.danilo.tcc.stock.core.application.product.query.ProductQuery
import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.product.ProductId

object ProductHandlerTestFixture {
    fun productQuery() =
        ProductQuery(
            id = ProductId().toString(),
            name = "Product 1",
            description = "Product description",
            imageUrl = "https://example.com/image.jpg",
            price = 10.0,
            quantity = 100,
            categoryId = CategoryId().toString(),
        )

    fun anotherProductQuery() =
        ProductQuery(
            id = ProductId().toString(),
            name = "Product 2",
            description = "Product description 2",
            imageUrl = "https://example.com/image2.jpg",
            price = 20.0,
            quantity = 200,
            categoryId = CategoryId().toString(),
        )
}
