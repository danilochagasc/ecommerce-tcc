package com.danilo.tcc.stock.core.application.product

import com.danilo.tcc.stock.core.application.product.command.CreateProductCommand
import com.danilo.tcc.stock.core.application.product.command.UpdateProductCommand
import com.danilo.tcc.stock.core.domain.product.Product
import com.danilo.tcc.stock.core.domain.product.ProductAlreadyExistsException
import com.danilo.tcc.stock.core.domain.product.ProductId
import com.danilo.tcc.stock.core.domain.product.ProductNotFoundException
import com.danilo.tcc.stock.core.domain.product.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val repository: ProductRepository,
) {
    suspend fun findById(id: ProductId): Product = repository.findById(id) ?: throw ProductNotFoundException(id)

    suspend fun findAll(): List<Product> = repository.findAll()

    suspend fun create(command: CreateProductCommand): ProductId {
        repository.existsByName(command.name).let { exists ->
            if (exists) {
                throw ProductAlreadyExistsException(command.name)
            }
        }

        val product =
            Product.create(
                name = command.name,
                description = command.description,
                imageUrl = command.imageUrl,
                price = command.price,
                quantity = command.quantity,
                categoryId = command.categoryId,
            )

        repository.create(product)

        return product.id
    }

    suspend fun update(command: UpdateProductCommand) {
        val product = repository.findById(command.id) ?: throw ProductNotFoundException(command.id)

        val updatedProduct =
            product.update(
                name = command.name,
                description = command.description,
                imageUrl = command.imageUrl,
                price = command.price,
                quantity = command.quantity,
                categoryId = command.categoryId,
            )

        repository.update(updatedProduct)
    }

    suspend fun delete(id: ProductId) {
        val product = repository.findById(id) ?: throw ProductNotFoundException(id)

        repository.delete(product.id)
    }
}
