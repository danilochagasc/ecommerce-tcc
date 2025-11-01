package com.br.danilo.tcc.checkout.core.domain.cart

import com.br.danilo.tcc.checkout.core.domain.coupon.Coupon
import com.br.danilo.tcc.checkout.core.domain.coupon.DiscountType
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

object CartTestFixture {
    fun cartId() = CartId("88553dc8-a71e-4858-9b71-7bd7dc945d4e")

    fun productId() = ProductId()

    fun cart() = Cart.create(cartId())

    fun cartWithItems(): Cart {
        val cart = Cart.create(cartId())
        return cart
            .addItem(productId(), "Product 1", 10.0, 2)
            .addItem(ProductId(), "Product 2", 20.0, 1)
    }

    fun cartItem(
        productId: ProductId = productId(),
        name: String = "Product Name",
        price: Double = 10.0,
        quantity: Int = 1,
    ) = CartItem(
        productId = productId,
        name = name,
        price = price,
        quantity = quantity,
    )

    fun coupon(
        code: String = "PROMO10",
        discountType: DiscountType = DiscountType.PERCENTAGE,
        value: Double = 10.0,
        expiresAt: Instant = Instant.parse("2025-12-01T19:30:44.974738Z"),
    ) = Coupon.create(
        code = code,
        discountType = discountType.name,
        value = value,
        expiresAt = expiresAt,
    )

    fun expiredCoupon(
        code: String = "PROMO10",
        discountType: DiscountType = DiscountType.PERCENTAGE,
        value: Double = 10.0,
        expiresAt: Instant = now() - 30.days,
    ) = Coupon.create(
        code = code,
        discountType = discountType.name,
        value = value,
        expiresAt = expiresAt,
    )

    fun cartWithCoupon(): Cart {
        val cart = cartWithItems()
        return cart.applyCoupon(coupon())
    }
}
