package com.br.danilo.tcc.checkout.core.domain.cart

import com.br.danilo.tcc.checkout.core.domain.AggregateId
import com.br.danilo.tcc.checkout.core.domain.coupon.Coupon
import org.valiktor.functions.isNotNull
import org.valiktor.validate

typealias CartId = AggregateId

data class Cart(
    val id: CartId,
    val items: List<CartItem>,
    val coupon: Coupon?,
) {
    init {
        validate()
    }

    private fun validate() {
        validate(this) {
            validate(Cart::id).isNotNull()
            validate(Cart::items).isNotNull()
        }
    }

    companion object {
        fun create(id: CartId) =
            Cart(
                id = id,
                items = emptyList(),
                coupon = null,
            )
    }

    fun addItem(
        productId: ProductId,
        name: String,
        price: Double,
        quantity: Int = 1,
    ): Cart {
        val updatedItems = items.toMutableList()
        val existing = updatedItems.find { it.productId == productId }

        if (existing == null) {
            updatedItems.add(CartItem(productId, name, price, quantity))
        }

        return copy(items = updatedItems)
    }

    fun increaseItemQuantity(
        productId: ProductId,
        quantity: Int = 1,
    ): Cart {
        val updatedItems = items.toMutableList()
        val existing = updatedItems.find { it.productId == productId }

        if (existing != null) {
            updatedItems[updatedItems.indexOf(existing)] =
                existing.copy(quantity = existing.quantity + quantity)
        }
        return copy(items = updatedItems)
    }

    fun removeItem(productId: ProductId): Cart {
        val updatedItems = items.toMutableList()
        val existing = updatedItems.find { it.productId == productId }

        if (existing != null) {
            updatedItems.remove(existing)
        }
        return copy(items = updatedItems)
    }

    fun decreaseItemQuantity(
        productId: ProductId,
        quantity: Int = 1,
    ): Cart {
        val updatedItems = items.toMutableList()
        val existing = updatedItems.find { it.productId == productId }

        if (existing != null) {
            if (existing.quantity > quantity) {
                updatedItems[updatedItems.indexOf(existing)] =
                    existing.copy(quantity = existing.quantity - quantity)
            } else {
                updatedItems.remove(existing)
            }
        }

        return copy(items = updatedItems)
    }

    fun applyCoupon(coupon: Coupon): Cart = copy(coupon = coupon)

    fun removeCoupon(): Cart = copy(coupon = null)

    fun isEmpty() = items.isEmpty()

    fun total(): Double {
        val subtotal = items.sumOf { it.price * it.quantity }
        return coupon?.applyDiscount(subtotal) ?: subtotal
    }
}
