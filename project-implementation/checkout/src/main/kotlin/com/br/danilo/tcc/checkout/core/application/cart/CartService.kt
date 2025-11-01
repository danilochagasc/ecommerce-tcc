package com.br.danilo.tcc.checkout.core.application.cart

import com.br.danilo.tcc.checkout.core.application.cart.command.AddCartItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.command.ApplyCouponCommand
import com.br.danilo.tcc.checkout.core.application.cart.command.IncreaseOrDecreaseItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.command.RemoveCartItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.query.toQuery
import com.br.danilo.tcc.checkout.core.application.coupon.CouponService
import com.br.danilo.tcc.checkout.core.domain.cart.Cart
import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartNotFoundException
import com.br.danilo.tcc.checkout.core.domain.cart.CartRepository
import org.springframework.stereotype.Service

@Service
class CartService(
    private val repository: CartRepository,
    private val couponService: CouponService,
) {
    suspend fun findById(cartId: CartId) = returnCartIfExists(cartId).toQuery()

    suspend fun addItem(command: AddCartItemCommand) {
        val cart = repository.findById(command.id) ?: Cart.create(command.id)
        val updated =
            cart.addItem(
                productId = command.item.productId,
                name = command.item.name,
                price = command.item.price,
                quantity = command.item.quantity,
            )
        repository.createOrUpdate(updated)
    }

    suspend fun increaseItemQuantity(command: IncreaseOrDecreaseItemCommand) {
        val cart = returnCartIfExists(command.cartId)
        val updated =
            cart.increaseItemQuantity(
                productId = command.productId,
                quantity = command.quantity,
            )
        repository.createOrUpdate(updated)
    }

    suspend fun removeItem(command: RemoveCartItemCommand) {
        val cart = returnCartIfExists(command.cartId)
        val updated = cart.removeItem(command.productId)
        updated.isEmpty().let {
            if (it) delete(command.cartId) else repository.createOrUpdate(updated)
        }
    }

    suspend fun decreaseItemQuantity(command: IncreaseOrDecreaseItemCommand) {
        val cart = returnCartIfExists(command.cartId)
        val updated =
            cart.decreaseItemQuantity(
                productId = command.productId,
                quantity = command.quantity,
            )
        updated.isEmpty().let {
            if (it) delete(command.cartId) else repository.createOrUpdate(updated)
        }
    }

    suspend fun applyCoupon(command: ApplyCouponCommand) {
        val cart = returnCartIfExists(command.id)
        couponService.returnCouponIfExists(command.couponCode).let {
            val updated = cart.applyCoupon(it)
            repository.createOrUpdate(updated)
        }
    }

    suspend fun removeCoupon(cartId: CartId) {
        val cart = returnCartIfExists(cartId)
        val updated = cart.removeCoupon()
        repository.createOrUpdate(updated)
    }

    suspend fun delete(cartId: CartId) {
        returnCartIfExists(cartId)
        repository.delete(cartId)
    }

    private suspend fun returnCartIfExists(cartId: CartId) = repository.findById(cartId) ?: throw CartNotFoundException(cartId)
}
