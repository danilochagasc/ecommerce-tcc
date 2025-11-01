package com.br.danilo.tcc.checkout.adapter.redis.cart

import com.br.danilo.tcc.checkout.adapter.redis.RedisTestContainer
import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartWithCoupon
import com.br.danilo.tcc.checkout.core.domain.cart.CartTestFixture.cartWithItems
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CartRedisRepositoryTest(
    private val cartRedisRepository: CartRedisRepository,
    private val reactiveStringRedisTemplate: ReactiveStringRedisTemplate,
) : DescribeSpec() {
    companion object {
        @Container
        @JvmStatic
        val redis: GenericContainer<*> = RedisTestContainer.createContainer()

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            RedisTestContainer.configureProperties(redis, registry)
        }
    }

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ): Unit =
        runBlocking {
            reactiveStringRedisTemplate.connectionFactory.reactiveConnection
                .serverCommands()
                .flushAll()
                .block()
        }

    init {
        describe("Use redis as a repository for carts") {

            context("Finding cart by id") {
                it("Should return cart when it exists") {
                    val cart = cartWithItems()

                    runBlocking {
                        cartRedisRepository.createOrUpdate(cart)
                        val result = cartRedisRepository.findById(cart.id)

                        result shouldNotBe null
                        result?.id shouldBe cart.id
                        result?.items?.size shouldBe cart.items.size
                    }
                }

                it("Should return null when cart does not exist") {
                    val cartId = CartId()

                    runBlocking {
                        val result = cartRedisRepository.findById(cartId)

                        result shouldBe null
                    }
                }
            }

            context("Creating or updating cart") {
                it("Should create cart") {
                    val cart = cartWithItems()

                    runBlocking {
                        val result = cartRedisRepository.createOrUpdate(cart)

                        result.id shouldBe cart.id
                        result.items.size shouldBe cart.items.size

                        val found = cartRedisRepository.findById(cart.id)
                        found shouldNotBe null
                        found?.id shouldBe cart.id
                    }
                }

                it("Should update existing cart") {
                    val cart = cartWithItems()
                    val updatedCart =
                        cart.addItem(
                            productId =
                                com.br.danilo.tcc.checkout.core.domain.cart
                                    .ProductId(),
                            name = "Product 3",
                            price = 30.0,
                            quantity = 1,
                        )

                    runBlocking {
                        cartRedisRepository.createOrUpdate(cart)
                        cartRedisRepository.createOrUpdate(updatedCart)

                        val result = cartRedisRepository.findById(cart.id)

                        result shouldNotBe null
                        result?.items?.size shouldBe updatedCart.items.size
                    }
                }

                it("Should save cart with coupon") {
                    val cart = cartWithCoupon()

                    runBlocking {
                        cartRedisRepository.createOrUpdate(cart)
                        val result = cartRedisRepository.findById(cart.id)

                        result shouldNotBe null
                        result?.coupon shouldNotBe null
                        result?.coupon?.code shouldBe cart.coupon?.code
                    }
                }
            }

            context("Deleting cart") {
                it("Should delete cart when it exists") {
                    val cart = cartWithItems()

                    runBlocking {
                        cartRedisRepository.createOrUpdate(cart)
                        cartRedisRepository.findById(cart.id) shouldNotBe null

                        cartRedisRepository.delete(cart.id)

                        val result = cartRedisRepository.findById(cart.id)
                        result shouldBe null
                    }
                }
            }
        }
    }
}
