package com.danilo.tcc.stock.core.domain.category

interface CategoryRepository {
    suspend fun findById(categoryId: CategoryId): Category?

    suspend fun findAll(): List<Category>

    suspend fun create(category: Category)

    suspend fun update(category: Category)

    suspend fun delete(categoryId: CategoryId)
}
