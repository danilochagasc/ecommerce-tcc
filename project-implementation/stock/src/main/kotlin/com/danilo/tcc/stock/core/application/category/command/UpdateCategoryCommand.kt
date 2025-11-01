package com.danilo.tcc.stock.core.application.category.command

import com.danilo.tcc.stock.core.domain.category.CategoryId

data class UpdateCategoryCommand(
    val id: CategoryId,
    val name: String,
)
