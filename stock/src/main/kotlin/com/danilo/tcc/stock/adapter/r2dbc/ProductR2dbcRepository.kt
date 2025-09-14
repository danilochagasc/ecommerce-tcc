package com.danilo.tcc.stock.adapter.r2dbc

import com.danilo.tcc.stock.core.domain.product.Product
import com.danilo.tcc.stock.core.domain.product.ProductId
import com.danilo.tcc.stock.core.domain.product.ProductRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class ProductR2dbcRepository(
    private val db: DatabaseClient,
) : ProductRepository {
    override suspend fun findById(id: ProductId): Product? {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): List<Product> {
        TODO("Not yet implemented")
    }

    override suspend fun create(product: Product) {
        TODO("Not yet implemented")
    }

    override suspend fun update(product: Product) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: ProductId) {
        TODO("Not yet implemented")
    }
}
