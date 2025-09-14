package com.danilo.tcc.stock.core.domain.product

interface ProductRepository {
    suspend fun findById(id: ProductId): Product?

    suspend fun findAll(): List<Product>

    suspend fun existsByName(name: String): Boolean

    suspend fun create(product: Product)

    suspend fun update(product: Product)

    suspend fun delete(id: ProductId)
}
