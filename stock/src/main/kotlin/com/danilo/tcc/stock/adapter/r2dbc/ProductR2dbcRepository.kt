package com.danilo.tcc.stock.adapter.r2dbc

import com.danilo.tcc.stock.adapter.r2dbc.queries.ProductSqlQueries.deleteProduct
import com.danilo.tcc.stock.adapter.r2dbc.queries.ProductSqlQueries.insertProduct
import com.danilo.tcc.stock.adapter.r2dbc.queries.ProductSqlQueries.selectProduct
import com.danilo.tcc.stock.adapter.r2dbc.queries.ProductSqlQueries.updateProduct
import com.danilo.tcc.stock.adapter.r2dbc.queries.ProductSqlQueries.whereId
import com.danilo.tcc.stock.adapter.r2dbc.queries.ProductSqlQueries.whereName
import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.product.Product
import com.danilo.tcc.stock.core.domain.product.ProductId
import com.danilo.tcc.stock.core.domain.product.ProductRepository
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.toList
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository

@Repository
class ProductR2dbcRepository(
    private val db: DatabaseClient,
) : ProductRepository {
    override suspend fun findById(id: ProductId): Product? =
        selectProduct()
            .where(whereId(id))
            .run {
                db
                    .sql(this)
                    .bindIfNotNull("id", id.toUUID())
                    .map { row, _ -> row.toProduct() }
                    .awaitOneOrNull()
            }

    override suspend fun findAll(): List<Product> =
        selectProduct()
            .run {
                db
                    .sql(this)
                    .map { row, _ -> row.toProduct() }
                    .flow()
                    .toList()
            }

    override suspend fun existsByName(name: String): Boolean =
        selectProduct()
            .where(whereName(name))
            .run {
                db
                    .sql(this)
                    .bind("name", name)
                    .map { row, _ -> row.toProduct() }
                    .awaitOneOrNull() != null
            }

    override suspend fun create(product: Product) {
        db
            .sql(insertProduct())
            .bind("id", product.id.toUUID())
            .bind("name", product.name)
            .bind("description", product.description)
            .bind("image_url", product.imageUrl)
            .bind("price", product.price)
            .bind("quantity", product.quantity)
            .bind("category_id", product.categoryId.toUUID())
            .await()
    }

    override suspend fun update(product: Product) {
        db
            .sql(updateProduct())
            .bind("name", product.name)
            .bind("description", product.description)
            .bind("image_url", product.imageUrl)
            .bind("price", product.price)
            .bind("quantity", product.quantity)
            .bind("category_id", product.categoryId.toUUID())
            .bind("id", product.id.toUUID())
            .await()
    }

    override suspend fun delete(id: ProductId) {
        db
            .sql(deleteProduct())
            .bind("id", id.toUUID())
            .await()
    }

    private fun Row.toProduct() =
        Product(
            id = ProductId(this.get<String>("id")),
            name = this.get<String>("name"),
            description = this.get<String>("description"),
            imageUrl = this.get<String>("image_url"),
            price = this.get<Double>("price"),
            quantity = this.get<Int>("quantity"),
            categoryId = CategoryId(this.get<String>("category_id")),
        )
}
