package com.danilo.tcc.stock.core.domain.category

object CategoryTestFixture {
    fun category(
        id: CategoryId = CategoryId(),
        name: String = "Category 1",
    ) = Category.create(name = name)

    fun anotherCategory() = category(name = "Category 2")
}
