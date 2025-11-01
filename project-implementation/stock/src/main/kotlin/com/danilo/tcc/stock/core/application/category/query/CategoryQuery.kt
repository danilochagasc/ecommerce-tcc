package com.danilo.tcc.stock.core.application.category.query

import com.danilo.tcc.stock.core.domain.category.Category

data class CategoryQuery(
    val id: String,
    val name: String,
)

fun Category.toQuery() =
    CategoryQuery(
        id = this.id.toString(),
        name = this.name,
    )
