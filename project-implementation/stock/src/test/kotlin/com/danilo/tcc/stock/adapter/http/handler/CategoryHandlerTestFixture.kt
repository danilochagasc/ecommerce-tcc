package com.danilo.tcc.stock.adapter.http.handler

import com.danilo.tcc.stock.core.application.category.query.CategoryQuery
import com.danilo.tcc.stock.core.domain.category.CategoryId

object CategoryHandlerTestFixture {
    fun categoryQuery() =
        CategoryQuery(
            id = CategoryId().toString(),
            name = "Category 1",
        )

    fun anotherCategoryQuery() =
        CategoryQuery(
            id = CategoryId().toString(),
            name = "Category 2",
        )
}
