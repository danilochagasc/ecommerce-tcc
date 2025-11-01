package com.danilo.tcc.stock.core.application.product

import com.danilo.tcc.stock.core.application.product.command.CreateProductCommand
import com.danilo.tcc.stock.core.application.product.command.DecreaseQuantityCommand
import com.danilo.tcc.stock.core.application.product.command.UpdateProductCommand
import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.product.ProductId

object ProductServiceTestFixture {
    fun createProductCommand(
        name: String = "Product 1",
        description: String = "Product description",
        price: Double = 10.0,
        quantity: Int = 100,
        categoryId: CategoryId = CategoryId(),
    ) = CreateProductCommand(
        name = name,
        description = description,
        price = price,
        quantity = quantity,
        categoryId = categoryId,
    )

    fun updateProductCommand(
        id: ProductId = ProductId(),
        name: String = "Updated Product",
        description: String = "Updated description",
        price: Double = 15.0,
        quantity: Int = 150,
        categoryId: CategoryId = CategoryId(),
    ) = UpdateProductCommand(
        id = id,
        name = name,
        description = description,
        price = price,
        quantity = quantity,
        categoryId = categoryId,
    )

    fun decreaseQuantityCommand(
        productId: ProductId = ProductId(),
        amount: Int = 10,
    ) = DecreaseQuantityCommand(
        productId = productId,
        amount = amount,
    )
}
