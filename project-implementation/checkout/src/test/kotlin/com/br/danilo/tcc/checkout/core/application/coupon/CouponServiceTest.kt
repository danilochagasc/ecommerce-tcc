package com.br.danilo.tcc.checkout.core.application.coupon

import com.br.danilo.tcc.checkout.core.application.coupon.CouponServiceTestFixture.createCouponCommand
import com.br.danilo.tcc.checkout.core.application.coupon.CouponServiceTestFixture.updateCouponCommand
import com.br.danilo.tcc.checkout.core.domain.coupon.Coupon
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponAlreadyExistsException
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponNotFoundException
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponRepository
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.coupon
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class CouponServiceTest : DescribeSpec() {
    private val couponRepository = mockk<CouponRepository>()

    private val couponService =
        CouponService(
            repository = couponRepository,
        )

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(couponRepository)
    }

    init {
        describe("Finding all coupons") {
            context("Coupons exist") {
                it("Should return all coupons") {
                    val coupons =
                        listOf(
                            coupon(code = "PROMO10"),
                            coupon(code = "PROMO20"),
                        )

                    coEvery { couponRepository.findAll() } returns coupons

                    val result = couponService.findAll()

                    result.size shouldBe 2
                    result.map { it.code } shouldBe listOf("PROMO10", "PROMO20")
                    coVerify { couponRepository.findAll() }
                }
            }

            context("No coupons exist") {
                it("Should return empty list") {
                    coEvery { couponRepository.findAll() } returns emptyList()

                    val result = couponService.findAll()

                    result shouldBe emptyList()
                    coVerify { couponRepository.findAll() }
                }
            }
        }

        describe("Finding coupon by code") {
            context("Coupon exists") {
                it("Should return coupon") {
                    val coupon = coupon()
                    coEvery { couponRepository.findByCode("PROMO10") } returns coupon

                    val result = couponService.findByCode("PROMO10")

                    result.code shouldBe coupon.code
                    coVerify { couponRepository.findByCode("PROMO10") }
                }
            }

            context("Coupon does not exist") {
                it("Should throw CouponNotFoundException") {
                    coEvery { couponRepository.findByCode("INVALID") } returns null

                    shouldThrow<CouponNotFoundException> {
                        couponService.findByCode("INVALID")
                    }

                    coVerify { couponRepository.findByCode("INVALID") }
                }
            }
        }

        describe("Creating coupon") {
            context("Valid coupon") {
                it("Should create coupon") {
                    val command = createCouponCommand()
                    val coupon =
                        Coupon.create(
                            code = command.code,
                            discountType = command.discountType,
                            value = command.value,
                            expiresAt = command.expiresAt,
                        )

                    coEvery { couponRepository.findByCode(command.code) } returns null
                    coEvery { couponRepository.createOrUpdate(any()) } returns coupon

                    val result = couponService.create(command)

                    result shouldBe command.code
                    coVerify { couponRepository.findByCode(command.code) }
                    coVerify { couponRepository.createOrUpdate(any()) }
                }
            }

            context("Coupon already exists") {
                it("Should throw CouponAlreadyExistsException") {
                    val command = createCouponCommand()
                    val existingCoupon = coupon(code = command.code)

                    coEvery { couponRepository.findByCode(command.code) } returns existingCoupon

                    shouldThrow<CouponAlreadyExistsException> {
                        couponService.create(command)
                    }

                    coVerify { couponRepository.findByCode(command.code) }
                    coVerify(exactly = 0) { couponRepository.createOrUpdate(any()) }
                }
            }
        }

        describe("Updating coupon") {
            context("Coupon exists") {
                it("Should update coupon") {
                    val command = updateCouponCommand()
                    val existingCoupon = coupon(code = command.code)
                    val updatedCoupon =
                        existingCoupon.update(
                            discountType = command.discountType,
                            value = command.value,
                            expiresAt = command.expiresAt,
                        )

                    coEvery { couponRepository.findByCode(command.code) } returns existingCoupon
                    coEvery { couponRepository.createOrUpdate(any()) } returns updatedCoupon

                    couponService.update(command)

                    coVerify { couponRepository.findByCode(command.code) }
                    coVerify { couponRepository.createOrUpdate(any()) }
                }
            }

            context("Coupon does not exist") {
                it("Should throw CouponNotFoundException") {
                    val command = updateCouponCommand()

                    coEvery { couponRepository.findByCode(command.code) } returns null

                    shouldThrow<CouponNotFoundException> {
                        couponService.update(command)
                    }

                    coVerify { couponRepository.findByCode(command.code) }
                    coVerify(exactly = 0) { couponRepository.createOrUpdate(any()) }
                }
            }
        }

        describe("Deleting coupon") {
            context("Coupon exists") {
                it("Should delete coupon") {
                    val code = "PROMO10"
                    val existingCoupon = coupon(code = code)

                    coEvery { couponRepository.findByCode(code) } returns existingCoupon
                    coEvery { couponRepository.delete(code) } returns Unit

                    couponService.delete(code)

                    coVerify { couponRepository.findByCode(code) }
                    coVerify { couponRepository.delete(code) }
                }
            }

            context("Coupon does not exist") {
                it("Should throw CouponNotFoundException") {
                    val code = "INVALID"

                    coEvery { couponRepository.findByCode(code) } returns null

                    shouldThrow<CouponNotFoundException> {
                        couponService.delete(code)
                    }

                    coVerify { couponRepository.findByCode(code) }
                    coVerify(exactly = 0) { couponRepository.delete(any()) }
                }
            }
        }
    }
}
