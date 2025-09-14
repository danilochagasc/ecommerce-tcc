package com.danilo.tcc.stock.core.domain.category

class CategoryNotFoundException(
    val categoryId: CategoryId,
) : Exception("Category with id $categoryId not found")

class CategoryAlreadyExistsException(
    val name: String,
) : Exception("Category with name '$name' already exists")
