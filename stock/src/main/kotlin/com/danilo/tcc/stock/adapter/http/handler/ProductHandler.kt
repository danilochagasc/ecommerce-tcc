package com.danilo.tcc.stock.adapter.http.handler

import com.danilo.tcc.stock.adapter.http.request.UpdateProductRequest
import com.danilo.tcc.stock.core.application.product.ProductService
import com.danilo.tcc.stock.core.application.product.command.CreateProductCommand
import com.danilo.tcc.stock.core.application.product.command.UpdateProductCommand
import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.product.ProductId
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class ProductHandler(
    private val service: ProductService,
) {
    suspend fun findById(req: ServerRequest): ServerResponse {
        val productId = ProductId(req.pathVariable("id"))
        return ok().bodyValueAndAwait(service.findById(productId))
    }

    suspend fun findAll(req: ServerRequest): ServerResponse = ok().bodyValueAndAwait(service.findAll())

    suspend fun create(req: ServerRequest): ServerResponse {
        val command = req.awaitBody<CreateProductCommand>()
        val id = service.create(command)
        return created(req.uriBuilder().path("/{id}").build(id)).buildAndAwait()
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val command = req.awaitBody<UpdateProductRequest>()
        service.update(
            UpdateProductCommand(
                id = ProductId(req.pathVariable("id")),
                name = command.name,
                description = command.description,
                imageUrl = command.imageUrl,
                price = command.price,
                quantity = command.quantity,
                categoryId = command.categoryId,
            ),
        )
        return ok().buildAndAwait()
    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val categoryId = CategoryId(req.pathVariable("id"))
        service.delete(categoryId)
        return noContent().buildAndAwait()
    }
}
