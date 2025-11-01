package com.br.danilo.tcc.checkout.core.application.cart

import com.br.danilo.tcc.checkout.core.application.cart.CartServiceTestFixture.addCartItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.CartServiceTestFixture.applyCouponCommand
import com.br.danilo.tcc.checkout.core.application.cart.CartServiceTestFixture.decreaseItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.CartServiceTestFixture.increaseItemCommand
import com.br.danilo.tcc.checkout.core.application.cart.CartServiceTestFixture.removeCartItemCommand
import com.br.danilo.tcc.checkout.core.application.coupon.CouponService
import com.br.danilo.tcc.checkout.core.domain.cart.Cart
import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartNotFoundException
import com.br.danilo.tcc.checkout.core.domain.cart.CartRepository
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cart
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartItem
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartWithItems
import com.br.danilo.tcc.checkout.core.domain.coupon.Coupon
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days

class CartServiceTest : DescribeSpec() {
    private val cartRepository = mockk<CartRepository>()
    private val couponService = mockk<CouponService>()

    private val cartService =
        CartService(
            repository = cartRepository,
            couponService = couponService,
        )

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(cartRepository, couponService)
    }

    init {
        describe("Finding cart by id") {
            context("Cart exists") {
                it("Should return cart") {
                    val cart = cartWithItems()
                    coEvery { cartRepository.findById(cart.id) } returns cart

                    val result = cartService.findById(cart.id)

                    result.id shouldBe cart.id.toString()
                    result.items.size shouldBe cart.items.size
                    coVerify { cartRepository.findById(cart.id) }
                }
            }

            context("Cart does not exist") {
                it("Should throw CartNotFoundException") {
                    val cartId = CartId()
                    coEvery { cartRepository.findById(cartId) } returns null

                    shouldThrow<CartNotFoundException> {
                        cartService.findById(cartId)
                    }

                    coVerify { cartRepository.findById(cartId) }
                }
            }
        }

        describe("Adding item to cart") {
            context("Cart exists") {
                it("Should add item to existing cart") {
                    val cart = cart()
                    val command = addCartItemCommand(cartId = cart.id)
                    val updatedCart =
                        cart.addItem(
                            command.item.productId,
                            command.item.name,
                            command.item.price,
                            command.item.quantity,
                        )

                    coEvery { cartRepository.findById(cart.id) } returns cart
                    coEvery { cartRepository.createOrUpdate(any()) } returns updatedCart

                    cartService.addItem(command)

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { cartRepository.createOrUpdate(any()) }
                }
            }

            context("Cart does not exist") {
                it("Should create new cart with item") {
                    val cartId = CartId()
                    val command = addCartItemCommand(cartId = cartId)
                    val newCart =
                        Cart.create(cartId).addItem(
                            command.item.productId,
                            command.item.name,
                            command.item.price,
                            command.item.quantity,
                        )

                    coEvery { cartRepository.findById(cartId) } returns null
                    coEvery { cartRepository.createOrUpdate(any()) } returns newCart

                    cartService.addItem(command)

                    coVerify { cartRepository.findById(cartId) }
                    coVerify { cartRepository.createOrUpdate(any()) }
                }
            }
        }

        describe("Increasing item quantity") {
            context("Cart exists with item") {
                it("Should increase quantity") {
                    val cart = cartWithItems()
                    val productId = cart.items.first().productId
                    val command =
                        increaseItemCommand(
                            cartId = cart.id,
                            productId = productId,
                            quantity = 2,
                        )
                    val updatedCart = cart.increaseItemQuantity(productId, 2)

                    coEvery { cartRepository.findById(cart.id) } returns cart
                    coEvery { cartRepository.createOrUpdate(any()) } returns updatedCart

                    cartService.increaseItemQuantity(command)

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { cartRepository.createOrUpdate(any()) }
                }
            }

            context("Cart does not exist") {
                it("Should throw CartNotFoundException") {
                    val cartId = CartId()
                    val command = increaseItemCommand(cartId = cartId)

                    coEvery { cartRepository.findById(cartId) } returns null

                    shouldThrow<CartNotFoundException> {
                        cartService.increaseItemQuantity(command)
                    }

                    coVerify { cartRepository.findById(cartId) }
                    coVerify(exactly = 0) { cartRepository.createOrUpdate(any()) }
                }
            }
        }

        describe("Decreasing item quantity") {
            context("Cart exists with item") {
                it("Should decrease quantity") {
                    val cart = cartWithItems()
                    val productId = cart.items.first().productId
                    val command =
                        decreaseItemCommand(
                            cartId = cart.id,
                            productId = productId,
                            quantity = 1,
                        )
                    val updatedCart = cart.decreaseItemQuantity(productId, 1)

                    coEvery { cartRepository.findById(cart.id) } returns cart
                    coEvery { cartRepository.createOrUpdate(any()) } returns updatedCart

                    cartService.decreaseItemQuantity(command)

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { cartRepository.createOrUpdate(any()) }
                }

                it("Should delete cart when becomes empty") {
                    val cart =
                        cart().addItem(
                            productId = cartItem().productId,
                            name = "Product",
                            price = 10.0,
                            quantity = 1,
                        )
                    val productId = cart.items.first().productId
                    val command =
                        decreaseItemCommand(
                            cartId = cart.id,
                            productId = productId,
                            quantity = 1,
                        )
                    val emptyCart = cart.decreaseItemQuantity(productId, 1)

                    coEvery { cartRepository.findById(cart.id) } returns cart
                    coEvery { cartRepository.delete(cart.id) } returns Unit

                    cartService.decreaseItemQuantity(command)

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { cartRepository.delete(cart.id) }
                    coVerify(exactly = 0) { cartRepository.createOrUpdate(any()) }
                }
            }
        }

        describe("Removing item from cart") {
            context("Cart exists with item") {
                it("Should remove item") {
                    val cart = cartWithItems()
                    val productId = cart.items.first().productId
                    val command =
                        removeCartItemCommand(
                            cartId = cart.id,
                            productId = productId,
                        )
                    val updatedCart = cart.removeItem(productId)

                    coEvery { cartRepository.findById(cart.id) } returns cart
                    coEvery { cartRepository.createOrUpdate(any()) } returns updatedCart

                    cartService.removeItem(command)

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { cartRepository.createOrUpdate(any()) }
                }

                it("Should delete cart when becomes empty") {
                    val cart =
                        cart().addItem(
                            productId = cartItem().productId,
                            name = "Product",
                            price = 10.0,
                            quantity = 1,
                        )
                    val productId = cart.items.first().productId
                    val command =
                        removeCartItemCommand(
                            cartId = cart.id,
                            productId = productId,
                        )

                    coEvery { cartRepository.findById(cart.id) } returns cart
                    coEvery { cartRepository.delete(cart.id) } returns Unit

                    cartService.removeItem(command)

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { cartRepository.delete(cart.id) }
                    coVerify(exactly = 0) { cartRepository.createOrUpdate(any()) }
                }
            }

            context("Cart does not exist") {
                it("Should throw CartNotFoundException") {
                    val cartId = CartId()
                    val command = removeCartItemCommand(cartId = cartId)

                    coEvery { cartRepository.findById(cartId) } returns null

                    shouldThrow<CartNotFoundException> {
                        cartService.removeItem(command)
                    }

                    coVerify { cartRepository.findById(cartId) }
                }
            }
        }

        describe("Applying coupon to cart") {
            context("Cart and coupon exist") {
                it("Should apply coupon") {
                    val cart = cartWithItems()
                    val coupon =
                        com.br.danilo.tcc.checkout.core.domain.coupon.Coupon.create(
                            code = "PROMO10",
                            discountType = "PERCENTAGE",
                            value = 10.0,
                            expiresAt = now() + 30.days,
                        )
                    val command =
                        applyCouponCommand(
                            cartId = cart.id,
                            couponCode = "PROMO10",
                        )
                    val updatedCart = cart.applyCoupon(coupon)

                    coEvery { cartRepository.findById(cart.id) } returns cart
                    coEvery { couponService.returnCouponIfExists("PROMO10") } returns coupon
                    coEvery { cartRepository.createOrUpdate(any()) } returns updatedCart

                    cartService.applyCoupon(command)

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { couponService.returnCouponIfExists("PROMO10") }
                    coVerify { cartRepository.createOrUpdate(any()) }
                }
            }

            context("Coupon does not exist") {
                it("Should throw CouponNotFoundException") {
                    val cart = cartWithItems()
                    val command =
                        applyCouponCommand(
                            cartId = cart.id,
                            couponCode = "INVALID",
                        )

                    coEvery { cartRepository.findById(cart.id) } returns cart
                    coEvery { couponService.returnCouponIfExists("INVALID") } throws CouponNotFoundException("INVALID")

                    shouldThrow<CouponNotFoundException> {
                        cartService.applyCoupon(command)
                    }

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { couponService.returnCouponIfExists("INVALID") }
                    coVerify(exactly = 0) { cartRepository.createOrUpdate(any()) }
                }
            }
        }

        describe("Removing coupon from cart") {
            context("Cart exists with coupon") {
                it("Should remove coupon") {
                    val cart = cartWithItems()
                    val coupon =
                        com.br.danilo.tcc.checkout.core.domain.coupon.Coupon.create(
                            code = "PROMO10",
                            discountType = "PERCENTAGE",
                            value = 10.0,
                            expiresAt = now() + 30.days,
                        )
                    val cartWithCoupon = cart.applyCoupon(coupon)
                    val updatedCart = cartWithCoupon.removeCoupon()

                    coEvery { cartRepository.findById(cart.id) } returns cartWithCoupon
                    coEvery { cartRepository.createOrUpdate(any()) } returns updatedCart

                    cartService.removeCoupon(cart.id)

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { cartRepository.createOrUpdate(any()) }
                }
            }
        }

        describe("Deleting cart") {
            context("Cart exists") {
                it("Should delete cart") {
                    val cart = cartWithItems()

                    coEvery { cartRepository.findById(cart.id) } returns cart
                    coEvery { cartRepository.delete(cart.id) } returns Unit

                    cartService.delete(cart.id)

                    coVerify { cartRepository.findById(cart.id) }
                    coVerify { cartRepository.delete(cart.id) }
                }
            }

            context("Cart does not exist") {
                it("Should throw CartNotFoundException") {
                    val cartId = CartId()

                    coEvery { cartRepository.findById(cartId) } returns null

                    shouldThrow<CartNotFoundException> {
                        cartService.delete(cartId)
                    }

                    coVerify { cartRepository.findById(cartId) }
                    coVerify(exactly = 0) { cartRepository.delete(any()) }
                }
            }
        }
    }
}
