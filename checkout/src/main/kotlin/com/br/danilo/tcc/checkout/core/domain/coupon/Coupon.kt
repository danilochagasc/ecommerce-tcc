package com.br.danilo.tcc.checkout.core.domain.coupon

import com.br.danilo.tcc.checkout.core.domain.cart.DiscountType
import com.br.danilo.tcc.checkout.core.domain.cart.DiscountType.FIXED
import com.br.danilo.tcc.checkout.core.domain.cart.DiscountType.PERCENTAGE
import org.valiktor.functions.isGreaterThan
import org.valiktor.functions.isLessThanOrEqualTo
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.isNotNull
import org.valiktor.validate
import kotlin.time.Clock.System.now
import kotlin.time.Instant

data class Coupon(
    val code: String,
    val discountType: DiscountType,
    val value: Double,
    val expiresAt: Instant,
) {
    init {
        validate()
    }

    private fun validate() {
        validate(this) {
            validate(Coupon::code).isNotEmpty()
            validate(Coupon::discountType).isNotNull()
            validate(Coupon::value).isNotNull().isGreaterThan(0.0)
            when (discountType) {
                PERCENTAGE -> validate(Coupon::value).isGreaterThan(0.0).isLessThanOrEqualTo(100.0)
                FIXED -> validate(Coupon::value).isGreaterThan(0.0)
            }
            validate(Coupon::expiresAt).isNotNull().isGreaterThan(now())
        }
    }

    companion object {
        fun create(
            code: String,
            discountType: String,
            value: Double,
            expiresAt: Instant,
        ) = Coupon(
            code = code,
            discountType = enumValueOf(discountType),
            value = value,
            expiresAt = expiresAt,
        )
    }

    fun update(
        discountType: String,
        value: Double,
        expiresAt: Instant,
    ): Coupon =
        this.copy(
            discountType = enumValueOf(discountType),
            value = value,
            expiresAt = expiresAt,
        )

    fun isValid(): Boolean = expiresAt > now()

    fun getTimeToLive() = expiresAt - now()

    fun applyDiscount(amount: Double): Double {
        if (!isValid()) return amount

        return when (discountType) {
            PERCENTAGE -> amount * (1 - value / 100)
            FIXED -> (amount - value).coerceAtLeast(0.0)
        }
    }
}
