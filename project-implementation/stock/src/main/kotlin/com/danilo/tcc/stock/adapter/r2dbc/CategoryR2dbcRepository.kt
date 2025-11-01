package com.danilo.tcc.stock.adapter.r2dbc

import com.danilo.tcc.stock.adapter.r2dbc.queries.CategorySqlQueries.deleteCategory
import com.danilo.tcc.stock.adapter.r2dbc.queries.CategorySqlQueries.insertCategory
import com.danilo.tcc.stock.adapter.r2dbc.queries.CategorySqlQueries.selectCategory
import com.danilo.tcc.stock.adapter.r2dbc.queries.CategorySqlQueries.updateCategory
import com.danilo.tcc.stock.adapter.r2dbc.queries.CategorySqlQueries.whereId
import com.danilo.tcc.stock.adapter.r2dbc.queries.CategorySqlQueries.whereName
import com.danilo.tcc.stock.core.domain.category.Category
import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.category.CategoryRepository
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.toList
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class CategoryR2dbcRepository(
    private val db: DatabaseClient,
) : CategoryRepository {
    override suspend fun findById(categoryId: CategoryId): Category? =
        selectCategory()
            .where(whereId(categoryId))
            .run {
                db
                    .sql(this)
                    .bindIfNotNull("id", categoryId.toUUID())
                    .map { row, _ ->
                        row.toCategory()
                    }.awaitOneOrNull()
            }

    override suspend fun findAll(): List<Category> =
        selectCategory()
            .run {
                db
                    .sql(this)
                    .map { row, _ -> row.toCategory() }
                    .flow()
                    .toList()
            }

    override suspend fun existsByName(name: String): Boolean =
        selectCategory()
            .where(whereName(name))
            .run {
                db
                    .sql(this)
                    .bind("name", name)
                    .map { row, _ -> row.toCategory() }
                    .awaitOneOrNull() != null
            }

    override suspend fun create(category: Category) {
        db
            .sql(insertCategory())
            .bind("id", category.id.toUUID())
            .bind("name", category.name)
            .await()
    }

    override suspend fun update(category: Category) {
        db
            .sql(updateCategory())
            .bind("id", category.id.toUUID())
            .bind("name", category.name)
            .await()
    }

    override suspend fun delete(categoryId: CategoryId) {
        db
            .sql(deleteCategory())
            .bind("id", categoryId.toUUID())
            .await()
    }

    private fun Row.toCategory() =
        Category(
            id = CategoryId(this.get<UUID>("id")),
            name = this.get<String>("name"),
        )
}
