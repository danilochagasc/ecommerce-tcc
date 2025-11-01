package com.br.danilo.tcc.checkout.adapter.http.handler

import com.br.danilo.tcc.checkout.adapter.http.handler.CartHandlerTestFixture.cartQuery
import com.br.danilo.tcc.checkout.adapter.http.handler.CartHandlerTestFixture.cartQueryWithCoupon
import com.br.danilo.tcc.checkout.adapter.router.createWebTestClient
import com.br.danilo.tcc.checkout.core.application.cart.CartService
import com.br.danilo.tcc.checkout.core.application.cart.query.CartQuery
import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartItem
import com.br.danilo.tcc.checkout.core.domain.cart.CartNotFoundException
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartId
import com.br.danilo.tcc.checkout.core.domain.cart.ProductId
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest
class CartHandlerTest(
    applicationContext: ApplicationContext,
) : DescribeSpec() {
    @MockkBean
    private lateinit var cartService: CartService

    private val webClient = createWebTestClient(applicationContext)

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(cartService)
    }

    init {
        describe("Finding cart by id") {

            context("Cart does not exist") {
                it("Should return 404 when cart does not exist") {
                    val cartId = cartId()
                    coEvery { cartService.findById(eq(cartId)) } throws CartNotFoundException(cartId)

                    webClient
                        .get()
                        .uri("/cart/$cartId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isNotFound

                    coVerify { cartService.findById(eq(cartId)) }
                    confirmVerified(cartService)
                }
            }

            context("Cart exists") {
                it("Should find cart and return 200") {
                    val cartQuery = cartQuery()
                    val cartId = CartId(cartQuery.id)

                    coEvery { cartService.findById(eq(cartId)) } returns cartQuery

                    webClient
                        .get()
                        .uri("/cart/$cartId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<CartQuery>()
                        .isEqualTo(cartQuery)

                    coVerify { cartService.findById(eq(cartId)) }
                    confirmVerified(cartService)
                }

                it("Should find cart with coupon and return 200") {
                    val cartQuery = cartQueryWithCoupon()
                    val cartId = CartId(cartQuery.id)

                    coEvery { cartService.findById(eq(cartId)) } returns cartQuery

                    webClient
                        .get()
                        .uri("/cart/$cartId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<CartQuery>()
                        .isEqualTo(cartQuery)

                    coVerify { cartService.findById(eq(cartId)) }
                    confirmVerified(cartService)
                }
            }
        }

        describe("Adding item to cart") {

            context("Valid request") {
                it("Should add item to cart and return 204") {
                    val cartId = cartId()
                    val cartItem = CartItem(ProductId(), "Product 1", 10.0, 1)

                    coEvery { cartService.addItem(any()) } returns Unit

                    webClient
                        .post()
                        .uri("/cart/$cartId")
                        .contentType(APPLICATION_JSON)
                        .bodyValue(cartItem)
                        .exchange()
                        .expectStatus()
                        .isNoContent
                        .expectBody()
                        .isEmpty

                    coVerify(exactly = 1) { cartService.addItem(any()) }
                    confirmVerified(cartService)
                }
            }
        }

        describe("Increasing item quantity") {

            context("Valid request") {
                it("Should increase quantity and return 204") {
                    val cartId = cartId()
                    val productId = ProductId()
                    val quantity = 2

                    coEvery { cartService.increaseItemQuantity(any()) } returns Unit

                    webClient
                        .put()
                        .uri("/cart/$cartId/item/$productId/increase/$quantity")
                        .exchange()
                        .expectStatus()
                        .isNoContent
                        .expectBody()
                        .isEmpty

                    coVerify(exactly = 1) { cartService.increaseItemQuantity(any()) }
                    confirmVerified(cartService)
                }
            }
        }

        describe("Decreasing item quantity") {

            context("Valid request") {
                it("Should decrease quantity and return 204") {
                    val cartId = cartId()
                    val productId = ProductId()
                    val quantity = 1

                    coEvery { cartService.decreaseItemQuantity(any()) } returns Unit

                    webClient
                        .put()
                        .uri("/cart/$cartId/item/$productId/decrease/$quantity")
                        .exchange()
                        .expectStatus()
                        .isNoContent
                        .expectBody()
                        .isEmpty

                    coVerify(exactly = 1) { cartService.decreaseItemQuantity(any()) }
                    confirmVerified(cartService)
                }
            }
        }

        describe("Removing item from cart") {

            context("Valid request") {
                it("Should remove item and return 204") {
                    val cartId = cartId()
                    val productId = ProductId()

                    coEvery { cartService.removeItem(any()) } returns Unit

                    webClient
                        .delete()
                        .uri("/cart/$cartId/item/$productId")
                        .exchange()
                        .expectStatus()
                        .isNoContent
                        .expectBody()
                        .isEmpty

                    coVerify(exactly = 1) { cartService.removeItem(any()) }
                    confirmVerified(cartService)
                }
            }
        }

        describe("Applying coupon to cart") {

            context("Valid request") {
                it("Should apply coupon and return 204") {
                    val cartId = cartId()
                    val couponCode = "PROMO10"

                    coEvery { cartService.applyCoupon(any()) } returns Unit

                    webClient
                        .put()
                        .uri("/cart/$cartId/coupon/$couponCode")
                        .exchange()
                        .expectStatus()
                        .isNoContent
                        .expectBody()
                        .isEmpty

                    coVerify(exactly = 1) { cartService.applyCoupon(any()) }
                    confirmVerified(cartService)
                }
            }
        }

        describe("Removing coupon from cart") {

            context("Valid request") {
                it("Should remove coupon and return 204") {
                    val cartId = cartId()

                    coEvery { cartService.removeCoupon(eq(cartId)) } returns Unit

                    webClient
                        .delete()
                        .uri("/cart/$cartId/coupon")
                        .exchange()
                        .expectStatus()
                        .isNoContent
                        .expectBody()
                        .isEmpty

                    coVerify { cartService.removeCoupon(eq(cartId)) }
                    confirmVerified(cartService)
                }
            }
        }

        describe("Deleting cart") {

            context("Valid request") {
                it("Should delete cart and return 204") {
                    val cartId = cartId()

                    coEvery { cartService.delete(eq(cartId)) } returns Unit

                    webClient
                        .delete()
                        .uri("/cart/$cartId")
                        .exchange()
                        .expectStatus()
                        .isNoContent
                        .expectBody()
                        .isEmpty

                    coVerify { cartService.delete(eq(cartId)) }
                    confirmVerified(cartService)
                }
            }
        }
    }
}
