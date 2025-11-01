package com.danilo.tcc.stock.adapter.r2dbc

import com.danilo.tcc.stock.core.domain.category.Category
import com.danilo.tcc.stock.core.domain.category.CategoryId
import java.util.UUID

object CategoryR2dbcRepositoryTestFixture {
    private val categoryId = CategoryId(UUID.fromString("8b1c7a4d-6e58-465e-bc34-b43cf7649b87"))
    private val anotherCategoryId = CategoryId(UUID.fromString("b06b2db2-14b3-4880-856b-af5c5c01c999"))

    fun categoryId() = categoryId

    fun anotherCategoryId() = anotherCategoryId

    fun category() =
        Category(
            id = categoryId,
            name = "Category 1",
        )

    fun anotherCategory() =
        Category(
            id = anotherCategoryId,
            name = "Category 2",
        )

    fun categoryUpdated() =
        Category(
            id = categoryId,
            name = "Updated Category",
        )
}
