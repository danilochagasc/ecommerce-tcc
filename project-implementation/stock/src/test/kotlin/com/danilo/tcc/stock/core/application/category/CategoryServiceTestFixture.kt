package com.danilo.tcc.stock.core.application.category

import com.danilo.tcc.stock.core.application.category.command.CreateCategoryCommand
import com.danilo.tcc.stock.core.application.category.command.UpdateCategoryCommand
import com.danilo.tcc.stock.core.domain.category.CategoryId

object CategoryServiceTestFixture {
    fun createCategoryCommand(name: String = "Category 1") = CreateCategoryCommand(name = name)

    fun updateCategoryCommand(
        id: CategoryId = CategoryId(),
        name: String = "Updated Category",
    ) = UpdateCategoryCommand(
        id = id,
        name = name,
    )
}
