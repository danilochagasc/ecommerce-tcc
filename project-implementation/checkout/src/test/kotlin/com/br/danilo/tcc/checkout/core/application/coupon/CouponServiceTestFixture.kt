package com.br.danilo.tcc.checkout.core.application.coupon

import com.br.danilo.tcc.checkout.core.application.coupon.command.CreateCouponCommand
import com.br.danilo.tcc.checkout.core.application.coupon.command.UpdateCouponCommand
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

object CouponServiceTestFixture {
    fun createCouponCommand(
        code: String = "PROMO10",
        discountType: String = "PERCENTAGE",
        value: Double = 10.0,
        expiresAt: Instant = now() + 30.days,
    ) = CreateCouponCommand(
        code = code,
        discountType = discountType,
        value = value,
        expiresAt = expiresAt,
    )

    fun updateCouponCommand(
        code: String = "PROMO10",
        discountType: String = "PERCENTAGE",
        value: Double = 15.0,
        expiresAt: Instant = now() + 30.days,
    ) = UpdateCouponCommand(
        code = code,
        discountType = discountType,
        value = value,
        expiresAt = expiresAt,
    )
}
