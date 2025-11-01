package com.br.danilo.tcc.checkout.adapter.services.stock

import com.br.danilo.tcc.checkout.core.domain.cart.ProductId

object StockRepositoryTestFixture {
    fun product(
        id: String = ProductId().toString(),
        name: String = "Product Name",
        description: String = "Product Description",
        imageUrl: String = "https://example.com/image.jpg",
        price: Double = 10.0,
        quantity: Int = 100,
        categoryId: String = "category-id",
    ) = Product(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        price = price,
        quantity = quantity,
        categoryId = categoryId,
    )

    fun productJson(product: Product = product()) =
        """
        {
            "id": "${product.id}",
            "name": "${product.name}",
            "description": "${product.description}",
            "imageUrl": "${product.imageUrl}",
            "price": ${product.price},
            "quantity": ${product.quantity},
            "categoryId": "${product.categoryId}"
        }
        """.trimIndent()
}
