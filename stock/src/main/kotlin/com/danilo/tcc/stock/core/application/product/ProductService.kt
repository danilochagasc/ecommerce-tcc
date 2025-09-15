package com.danilo.tcc.stock.core.application.product

import com.danilo.tcc.stock.core.application.product.command.CreateProductCommand
import com.danilo.tcc.stock.core.application.product.command.DecreaseQuantityCommand
import com.danilo.tcc.stock.core.application.product.command.UpdateProductCommand
import com.danilo.tcc.stock.core.domain.product.Product
import com.danilo.tcc.stock.core.domain.product.ProductAlreadyExistsException
import com.danilo.tcc.stock.core.domain.product.ProductId
import com.danilo.tcc.stock.core.domain.product.ProductImageRepository
import com.danilo.tcc.stock.core.domain.product.ProductNotFoundException
import com.danilo.tcc.stock.core.domain.product.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val repository: ProductRepository,
    private val imageRepository: ProductImageRepository,
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
                imageUrl = "",
                price = command.price,
                quantity = command.quantity,
                categoryId = command.categoryId,
            )

        repository.create(product)

        return product.id
    }

    suspend fun uploadImage(
        productId: ProductId,
        bytes: ByteArray,
        fileName: String,
        contentType: String?,
    ) {
        val imageUrl = imageRepository.uploadImage(productId, bytes, fileName, contentType)

        val product = repository.findById(productId) ?: throw ProductNotFoundException(productId)
        val updatedProduct = product.changeImage(imageUrl)
        repository.update(updatedProduct)
    }

    suspend fun update(command: UpdateProductCommand) {
        val product = repository.findById(command.id) ?: throw ProductNotFoundException(command.id)

        val updatedProduct =
            product.update(
                name = command.name,
                description = command.description,
                price = command.price,
                quantity = command.quantity,
                categoryId = command.categoryId,
            )

        repository.update(updatedProduct)
    }

    suspend fun decreaseQuantity(command: DecreaseQuantityCommand) {
        val product = repository.findById(command.productId) ?: throw ProductNotFoundException(command.productId)
        val updatedProduct = product.decreaseQuantity(command.amount)

        repository.update(updatedProduct)
    }

    suspend fun delete(id: ProductId) {
        val product = repository.findById(id) ?: throw ProductNotFoundException(id)

        repository.delete(product.id)
    }
}
