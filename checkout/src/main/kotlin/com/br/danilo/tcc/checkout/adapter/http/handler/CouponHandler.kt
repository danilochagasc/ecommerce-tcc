package com.br.danilo.tcc.checkout.adapter.http.handler

import com.br.danilo.tcc.checkout.adapter.http.request.CreateCouponRequest
import com.br.danilo.tcc.checkout.adapter.http.request.UpdateCouponRequest
import com.br.danilo.tcc.checkout.core.application.coupon.CouponService
import com.br.danilo.tcc.checkout.core.application.coupon.command.CreateCouponCommand
import com.br.danilo.tcc.checkout.core.application.coupon.command.UpdateCouponCommand
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import kotlin.time.Instant

@Component
class CouponHandler(
    private val service: CouponService,
) {
    suspend fun findByCode(req: ServerRequest): ServerResponse {
        val code = req.pathVariable("code").uppercase()
        return ok().bodyValueAndAwait(service.findByCode(code))
    }

    suspend fun findAll(req: ServerRequest): ServerResponse = ok().bodyValueAndAwait(service.findAll())

    suspend fun create(req: ServerRequest): ServerResponse {
        val request = req.awaitBody<CreateCouponRequest>()
        val code = service.create(
            CreateCouponCommand(
                code = request.code.uppercase(),
                discountType = request.discountType,
                value = request.value,
                expiresAt = Instant.parse(request.expiresAt),
            )
        )
        return created(req.uriBuilder().path("/{code}").build(code)).buildAndAwait()
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val request = req.awaitBody<UpdateCouponRequest>()
        service.update(
            UpdateCouponCommand(
                code = req.pathVariable("code").uppercase(),
                discountType = request.discountType,
                value = request.value,
                expiresAt = Instant.parse(request.expiresAt),
            ),
        )
        return noContent().buildAndAwait()
    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val code = req.pathVariable("code").uppercase()
        service.delete(code)
        return noContent().buildAndAwait()
    }
}
