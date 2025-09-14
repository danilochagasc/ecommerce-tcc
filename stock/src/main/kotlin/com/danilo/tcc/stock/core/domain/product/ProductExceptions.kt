package com.danilo.tcc.stock.core.domain.product

class ProductNotFoundException(
    val productId: ProductId,
) : Exception("Product with id $productId not found")

class ProductAlreadyExistsException(
    val name: String,
) : Exception("Product with name '$name' already exists")

class InsufficientProductQuantityException(
    val productId: ProductId,
    val availableQuantity: Int,
    val requestedQuantity: Int,
) : Exception(
        "Insufficient quantity for product with id $productId: available $availableQuantity, " +
            "requested $requestedQuantity",
    )
