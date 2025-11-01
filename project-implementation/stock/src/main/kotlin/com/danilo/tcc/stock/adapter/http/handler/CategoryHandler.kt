package com.danilo.tcc.stock.adapter.http.handler

import com.danilo.tcc.stock.adapter.http.request.UpdateCategoryRequest
import com.danilo.tcc.stock.core.application.category.CategoryService
import com.danilo.tcc.stock.core.application.category.command.CreateCategoryCommand
import com.danilo.tcc.stock.core.application.category.command.UpdateCategoryCommand
import com.danilo.tcc.stock.core.domain.category.CategoryId
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
class CategoryHandler(
    private val service: CategoryService,
) {
    suspend fun findById(req: ServerRequest): ServerResponse {
        val categoryId = CategoryId(req.pathVariable("id"))
        return ok().bodyValueAndAwait(service.findById(categoryId))
    }

    suspend fun findAll(req: ServerRequest): ServerResponse = ok().bodyValueAndAwait(service.findAll())

    suspend fun create(req: ServerRequest): ServerResponse {
        val command = req.awaitBody<CreateCategoryCommand>()
        val id = service.create(command)
        return created(req.uriBuilder().path("/{id}").build(id)).buildAndAwait()
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val command = req.awaitBody<UpdateCategoryRequest>()
        service.update(
            UpdateCategoryCommand(
                id = CategoryId(req.pathVariable("id")),
                name = command.name,
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
