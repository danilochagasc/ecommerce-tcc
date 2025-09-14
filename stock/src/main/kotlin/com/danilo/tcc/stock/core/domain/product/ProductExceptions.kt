package com.danilo.tcc.stock.core.domain.product

class ProductNotFoundException(
    val productId: ProductId,
) : Exception("Product with id $productId not found")

class ProductAlreadyExistsException(
    val name: String,
) : Exception("Product with name '$name' already exists")
