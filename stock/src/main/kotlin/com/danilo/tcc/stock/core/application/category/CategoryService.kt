package com.danilo.tcc.stock.core.application.category

import com.danilo.tcc.stock.core.application.category.command.CreateCategoryCommand
import com.danilo.tcc.stock.core.application.category.command.UpdateCategoryCommand
import com.danilo.tcc.stock.core.domain.category.Category
import com.danilo.tcc.stock.core.domain.category.CategoryAlreadyExistsException
import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.category.CategoryNotFoundException
import com.danilo.tcc.stock.core.domain.category.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val repository: CategoryRepository,
) {
    suspend fun findById(id: CategoryId): Category = repository.findById(id) ?: throw CategoryNotFoundException(id)

    suspend fun findAll(): List<Category> = repository.findAll()

    suspend fun create(command: CreateCategoryCommand): CategoryId {
        repository.existsByName(command.name).let { exists ->
            if (exists) {
                throw CategoryAlreadyExistsException(command.name)
            }
        }

        val category =
            Category.create(
                name = command.name,
            )

        repository.create(category)

        return category.id
    }

    suspend fun update(command: UpdateCategoryCommand) {
        val category = repository.findById(command.id) ?: throw CategoryNotFoundException(command.id)

        val updatedCategory =
            category.update(
                name = command.name,
            )

        repository.update(updatedCategory)
    }

    suspend fun delete(id: CategoryId) {
        val category = repository.findById(id) ?: throw CategoryNotFoundException(id)

        repository.delete(category.id)
    }
}
