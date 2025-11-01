package com.br.danilo.tcc.checkout.adapter.redis.coupon

import com.br.danilo.tcc.checkout.adapter.redis.RedisTestContainer
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.coupon
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.fixedDiscountCoupon
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.percentageDiscountCoupon
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
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CouponRedisRepositoryTest(
    private val couponRedisRepository: CouponRedisRepository,
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
        describe("Use redis as a repository for coupons") {

            context("Finding all coupons") {
                it("Should return all coupons when they exist") {
                    val coupon1 = coupon(code = "PROMO10")
                    val coupon2 = percentageDiscountCoupon(code = "PROMO20")

                    runBlocking {
                        couponRedisRepository.createOrUpdate(coupon1)
                        couponRedisRepository.createOrUpdate(coupon2)

                        val result = couponRedisRepository.findAll()

                        result.size shouldBe 2
                        result.map { it.code }.toSet() shouldBe setOf("PROMO10", "PROMO20")
                    }
                }

                it("Should return empty list when no coupons exist") {
                    runBlocking {
                        val result = couponRedisRepository.findAll()

                        result shouldBe emptyList()
                    }
                }
            }

            context("Finding coupon by code") {
                it("Should return coupon when it exists") {
                    val coupon = coupon()

                    runBlocking {
                        couponRedisRepository.createOrUpdate(coupon)
                        val result = couponRedisRepository.findByCode(coupon.code)

                        result shouldNotBe null
                        result?.code shouldBe coupon.code
                        result?.discountType shouldBe coupon.discountType
                        result?.value shouldBe coupon.value
                    }
                }

                it("Should return null when coupon does not exist") {
                    runBlocking {
                        val result = couponRedisRepository.findByCode("INVALID")

                        result shouldBe null
                    }
                }
            }

            context("Creating or updating coupon") {
                it("Should create coupon") {
                    val coupon = coupon()

                    runBlocking {
                        val result = couponRedisRepository.createOrUpdate(coupon)

                        result.code shouldBe coupon.code
                        result.discountType shouldBe coupon.discountType

                        val found = couponRedisRepository.findByCode(coupon.code)
                        found shouldNotBe null
                        found?.code shouldBe coupon.code
                    }
                }

                it("Should create percentage discount coupon") {
                    val coupon = percentageDiscountCoupon()

                    runBlocking {
                        couponRedisRepository.createOrUpdate(coupon)
                        val found = couponRedisRepository.findByCode(coupon.code)

                        found shouldNotBe null
                        found?.discountType shouldBe coupon.discountType
                    }
                }

                it("Should create fixed discount coupon") {
                    val coupon = fixedDiscountCoupon()

                    runBlocking {
                        couponRedisRepository.createOrUpdate(coupon)
                        val found = couponRedisRepository.findByCode(coupon.code)

                        found shouldNotBe null
                        found?.discountType shouldBe coupon.discountType
                    }
                }

                it("Should update existing coupon") {
                    val coupon = coupon()
                    val updatedCoupon =
                        coupon.update(
                            discountType = "FIXED",
                            value = 25.0,
                            expiresAt = now() + 60.days,
                        )

                    runBlocking {
                        couponRedisRepository.createOrUpdate(coupon)
                        couponRedisRepository.createOrUpdate(updatedCoupon)

                        val result = couponRedisRepository.findByCode(coupon.code)

                        result shouldNotBe null
                        result?.discountType shouldBe updatedCoupon.discountType
                        result?.value shouldBe updatedCoupon.value
                    }
                }
            }

            context("Deleting coupon") {
                it("Should delete coupon when it exists") {
                    val coupon = coupon()

                    runBlocking {
                        couponRedisRepository.createOrUpdate(coupon)
                        couponRedisRepository.findByCode(coupon.code) shouldNotBe null

                        couponRedisRepository.delete(coupon.code)

                        val result = couponRedisRepository.findByCode(coupon.code)
                        result shouldBe null
                    }
                }
            }
        }
    }
}
