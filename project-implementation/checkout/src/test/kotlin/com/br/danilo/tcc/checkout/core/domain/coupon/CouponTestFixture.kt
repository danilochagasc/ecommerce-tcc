package com.br.danilo.tcc.checkout.core.domain.coupon

import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

object CouponTestFixture {
    fun coupon(
        code: String = "PROMO10",
        discountType: DiscountType = DiscountType.PERCENTAGE,
        value: Double = 10.0,
        expiresAt: Instant = now() + 30.days,
    ) = Coupon.create(
        code = code,
        discountType = discountType.name,
        value = value,
        expiresAt = expiresAt,
    )

    fun expiredCoupon(
        code: String = "EXPIRED",
        discountType: DiscountType = DiscountType.PERCENTAGE,
        value: Double = 10.0,
    ) = Coupon.create(
        code = code,
        discountType = discountType.name,
        value = value,
        expiresAt = now() - 1.days,
    )

    fun fixedDiscountCoupon(
        code: String = "FIXED5",
        value: Double = 5.0,
        expiresAt: Instant = now() + 30.days,
    ) = Coupon.create(
        code = code,
        discountType = DiscountType.FIXED.name,
        value = value,
        expiresAt = expiresAt,
    )

    fun percentageDiscountCoupon(
        code: String = "PERCENT20",
        value: Double = 20.0,
        expiresAt: Instant = now() + 30.days,
    ) = Coupon.create(
        code = code,
        discountType = DiscountType.PERCENTAGE.name,
        value = value,
        expiresAt = expiresAt,
    )
}
