package com.br.danilo.tcc.checkout.adapter.http.handler

import com.br.danilo.tcc.checkout.adapter.http.request.CreateCouponRequest
import com.br.danilo.tcc.checkout.adapter.http.request.UpdateCouponRequest
import com.br.danilo.tcc.checkout.core.application.coupon.query.toQuery
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.coupon
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days

object CouponHandlerTestFixture {
    fun couponQuery() = coupon().toQuery()

    fun couponQuery(code: String = "PROMO10") = coupon(code = code).toQuery()

    fun couponQueries() =
        listOf(
            coupon(code = "PROMO10").toQuery(),
            coupon(code = "PROMO20").toQuery(),
        )

    fun createCouponRequest() =
        CreateCouponRequest(
            code = "PROMO10",
            discountType = "PERCENTAGE",
            value = 10.0,
            expiresAt = (now() + 30.days).toString(),
        )

    fun updateCouponRequest() =
        UpdateCouponRequest(
            discountType = "PERCENTAGE",
            value = 15.0,
            expiresAt = (now() + 30.days).toString(),
        )
}
