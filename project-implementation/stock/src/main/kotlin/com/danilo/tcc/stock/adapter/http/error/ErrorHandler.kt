package com.danilo.tcc.stock.adapter.http.error

import com.danilo.tcc.stock.core.domain.category.CategoryAlreadyExistsException
import com.danilo.tcc.stock.core.domain.category.CategoryNotFoundException
import com.danilo.tcc.stock.core.domain.product.InsufficientProductQuantityException
import com.danilo.tcc.stock.core.domain.product.ProductAlreadyExistsException
import com.danilo.tcc.stock.core.domain.product.ProductNotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import org.valiktor.ConstraintViolationException
import reactor.core.publisher.Mono
import java.time.Instant

@Serializable
data class ErrorResponse(
    val message: String,
    val status: Int,
    val timestamp: String = Instant.now().toString(),
)

@Component
@Order(-2)
class GlobalErrorHandler : WebExceptionHandler {
    private companion object {
        val LOGGER = KotlinLogging.logger { }
    }

    override fun handle(
        exchange: ServerWebExchange,
        ex: Throwable,
    ): Mono<Void> {
        LOGGER.error(ex) { "An error occurred while processing the request: ${ex.message}" }

        val (status, message) =
            when (ex) {
                is CategoryNotFoundException -> HttpStatus.NOT_FOUND to ex.message
                is CategoryAlreadyExistsException -> HttpStatus.CONFLICT to ex.message
                is ProductNotFoundException -> HttpStatus.NOT_FOUND to ex.message
                is ProductAlreadyExistsException -> HttpStatus.CONFLICT to ex.message
                is InsufficientProductQuantityException -> HttpStatus.BAD_REQUEST to ex.message
                is ConstraintViolationException -> HttpStatus.BAD_REQUEST to getConstraintViolationMessage(ex)
                else -> HttpStatus.INTERNAL_SERVER_ERROR to "An unexpected error occurred"
            }

        val errorResponse =
            ErrorResponse(
                message = message ?: "No message available",
                status = status.value(),
            )

        val response = exchange.response
        response.statusCode = status
        response.headers.contentType = MediaType.APPLICATION_JSON

        val buffer = response.bufferFactory().wrap(Json.encodeToString(errorResponse).toByteArray())

        return response.writeWith(mono { buffer })
    }

    private fun getConstraintViolationMessage(violation: ConstraintViolationException): String {
        val firstViolation = violation.constraintViolations.first()

        val property = firstViolation.property
        val constraintName = firstViolation.constraint.name
        val messageParams = firstViolation.constraint.messageParams

        return when (constraintName) {
            "NotBlank" -> "Field $property cannot be blank"
            "Email" -> "Field $property must be a valid email address"
            "Size" -> {
                val min = messageParams["min"]
                val max = messageParams["max"]
                when {
                    min != null && max != null -> "Field $property must be between $min and $max characters"
                    min != null -> "Field $property must be at least $min characters"
                    max != null -> "Field $property must be at most $max characters"
                    else -> "Field $property must have a valid size"
                }
            }
            else -> "Field $property is invalid"
        }
    }
}
