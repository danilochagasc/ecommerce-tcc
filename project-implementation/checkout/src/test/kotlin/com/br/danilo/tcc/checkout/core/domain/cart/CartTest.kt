package com.br.danilo.tcc.checkout.core.domain.cart

import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cart
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartWithCoupon
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartWithItems
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.coupon
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.productId
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.expiredCoupon
import com.br.danilo.tcc.checkout.core.domain.coupon.DiscountType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class CartTest : DescribeSpec() {
    init {
        describe("Creating a Cart") {
            context("A valid one") {
                val cart = cart()

                it("Should be created properly with empty items") {
                    cart.id shouldBe cart().id
                    cart.items shouldBe emptyList()
                    cart.coupon shouldBe null
                    cart.isEmpty() shouldBe true
                }
            }
        }

        describe("Adding items to Cart") {
            context("Add new item") {
                it("Should add item to cart") {
                    val cart = cart()
                    val productId = productId()
                    val updatedCart = cart.addItem(productId, "Product 1", 10.0, 2)

                    updatedCart.items.size shouldBe 1
                    updatedCart.items.first().productId shouldBe productId
                    updatedCart.items.first().name shouldBe "Product 1"
                    updatedCart.items.first().price shouldBe 10.0
                    updatedCart.items.first().quantity shouldBe 2
                    updatedCart.isEmpty() shouldBe false
                }

                it("Should add multiple items to cart") {
                    val cart = cart()
                    val productId1 = productId()
                    val productId2 = ProductId()

                    val updatedCart =
                        cart
                            .addItem(productId1, "Product 1", 10.0, 2)
                            .addItem(productId2, "Product 2", 20.0, 1)

                    updatedCart.items.size shouldBe 2
                    updatedCart.isEmpty() shouldBe false
                }
            }
        }

        describe("Increasing item quantity") {
            context("Item exists in cart") {
                it("Should increase quantity") {
                    val cart = cartWithItems()
                    val productId = cart.items.first().productId
                    val initialQuantity = cart.items.first().quantity

                    val updatedCart = cart.increaseItemQuantity(productId, 2)

                    updatedCart.items.first().quantity shouldBe (initialQuantity + 2)
                }
            }

            context("Item does not exist in cart") {
                it("Should not change cart") {
                    val cart = cartWithItems()
                    val newProductId = ProductId()
                    val initialItemCount = cart.items.size

                    val updatedCart = cart.increaseItemQuantity(newProductId, 1)

                    updatedCart.items.size shouldBe initialItemCount
                }
            }
        }

        describe("Decreasing item quantity") {
            context("Quantity greater than decrease amount") {
                it("Should decrease quantity") {
                    val cart = cartWithItems()
                    val productId = cart.items.first().productId
                    val initialQuantity = cart.items.first().quantity

                    val updatedCart = cart.decreaseItemQuantity(productId, 1)

                    updatedCart.items.first().quantity shouldBe (initialQuantity - 1)
                }
            }

            context("Quantity equals decrease amount") {
                it("Should remove item from cart") {
                    val cart = cartWithItems()
                    val productId = cart.items.first().productId
                    val initialItemCount = cart.items.size

                    val updatedCart = cart.decreaseItemQuantity(productId, cart.items.first().quantity)

                    updatedCart.items.size shouldBe (initialItemCount - 1)
                    updatedCart.items.find { it.productId == productId } shouldBe null
                }
            }

            context("Quantity less than decrease amount") {
                it("Should remove item from cart") {
                    val cart = cartWithItems()
                    val productId = cart.items.first().productId
                    val initialItemCount = cart.items.size

                    val updatedCart = cart.decreaseItemQuantity(productId, cart.items.first().quantity + 1)

                    updatedCart.items.size shouldBe (initialItemCount - 1)
                    updatedCart.items.find { it.productId == productId } shouldBe null
                }
            }

            context("Item does not exist") {
                it("Should not change cart") {
                    val cart = cartWithItems()
                    val newProductId = ProductId()
                    val initialItemCount = cart.items.size

                    val updatedCart = cart.decreaseItemQuantity(newProductId, 1)

                    updatedCart.items.size shouldBe initialItemCount
                }
            }
        }

        describe("Removing item from Cart") {
            context("Item exists") {
                it("Should remove item") {
                    val cart = cartWithItems()
                    val productId = cart.items.first().productId
                    val initialItemCount = cart.items.size

                    val updatedCart = cart.removeItem(productId)

                    updatedCart.items.size shouldBe (initialItemCount - 1)
                    updatedCart.items.find { it.productId == productId } shouldBe null
                }
            }

            context("Item does not exist") {
                it("Should not change cart") {
                    val cart = cartWithItems()
                    val newProductId = ProductId()
                    val initialItemCount = cart.items.size

                    val updatedCart = cart.removeItem(newProductId)

                    updatedCart.items.size shouldBe initialItemCount
                }
            }
        }

        describe("Applying coupon to Cart") {
            context("Valid coupon") {
                it("Should apply coupon") {
                    val cart = cartWithItems()
                    val couponToApply = coupon()

                    val updatedCart = cart.applyCoupon(couponToApply)

                    updatedCart.coupon shouldBe couponToApply
                }
            }
        }

        describe("Removing coupon from Cart") {
            context("Cart has coupon") {
                it("Should remove coupon") {
                    val cart = cartWithCoupon()
                    cart.coupon shouldBe coupon()

                    val updatedCart = cart.removeCoupon()

                    updatedCart.coupon shouldBe null
                }
            }

            context("Cart has no coupon") {
                it("Should remain without coupon") {
                    val cart = cartWithItems()

                    val updatedCart = cart.removeCoupon()

                    updatedCart.coupon shouldBe null
                }
            }
        }

        describe("Calculating cart total") {
            context("Cart without coupon") {
                it("Should return sum of all items") {
                    val cart = cartWithItems()
                    val expectedTotal = cart.items.sumOf { it.price * it.quantity }

                    cart.total() shouldBe expectedTotal
                }
            }

            context("Cart with percentage coupon") {
                it("Should apply percentage discount") {
                    val cart = cartWithItems()
                    val percentageCoupon = coupon(value = 10.0)
                    val cartWithCoupon = cart.applyCoupon(percentageCoupon)
                    val subtotal = cart.items.sumOf { it.price * it.quantity }
                    val expectedTotal = subtotal * 0.9

                    cartWithCoupon.total() shouldBe expectedTotal
                }
            }

            context("Cart with fixed discount coupon") {
                it("Should apply fixed discount") {
                    val cart = cartWithItems()
                    val fixedCoupon =
                        coupon(
                            discountType = DiscountType.FIXED,
                            value = 5.0,
                        )
                    val cartWithCoupon = cart.applyCoupon(fixedCoupon)
                    val subtotal = cart.items.sumOf { it.price * it.quantity }
                    val expectedTotal = (subtotal - 5.0).coerceAtLeast(0.0)

                    cartWithCoupon.total() shouldBe expectedTotal
                }
            }

            context("Cart with expired coupon") {
                it("Should not apply discount") {
                    val cart = cartWithItems()
                    val expiredCoupon = expiredCoupon()
                    val subtotal = cart.items.sumOf { it.price * it.quantity }
                    val cartWithCoupon = cart.applyCoupon(expiredCoupon)

                    cartWithCoupon.total() shouldBe subtotal
                }
            }
        }

        describe("Checking if cart is empty") {
            context("Cart has no items") {
                it("Should return true") {
                    cart().isEmpty() shouldBe true
                }
            }

            context("Cart has items") {
                it("Should return false") {
                    cartWithItems().isEmpty() shouldBe false
                }
            }
        }
    }
}
