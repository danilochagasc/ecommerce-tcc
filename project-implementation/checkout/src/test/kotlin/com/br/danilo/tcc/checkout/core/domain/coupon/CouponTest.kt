package com.br.danilo.tcc.checkout.core.domain.coupon

import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.coupon
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.expiredCoupon
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.fixedDiscountCoupon
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponTestFixture.percentageDiscountCoupon
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.valiktor.constraints.Greater
import org.valiktor.constraints.LessOrEqual
import org.valiktor.constraints.NotBlank
import org.valiktor.test.shouldFailValidation
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days

class CouponTest : DescribeSpec() {
    init {
        describe("Creating a Coupon") {
            context("A valid one") {
                val coupon = coupon()

                it("Should be created properly") {
                    coupon.code shouldBe "PROMO10"
                    coupon.discountType shouldBe DiscountType.PERCENTAGE
                    coupon.value shouldBe 10.0
                    coupon.isValid() shouldBe true
                }
            }

            context("Invalid coupon code") {
                it("Should fail when code is blank") {
                    shouldFailValidation<Coupon> {
                        Coupon.create(
                            code = "",
                            discountType = "PERCENTAGE",
                            value = 10.0,
                            expiresAt = now() + 30.days,
                        )
                    }.verify {
                        expect(Coupon::code, "", NotBlank)
                    }
                }
            }

            context("Invalid discount value") {
                it("Should fail when value is zero") {
                    shouldFailValidation<Coupon> {
                        Coupon.create(
                            code = "PROMO10",
                            discountType = "PERCENTAGE",
                            value = 0.0,
                            expiresAt = now() + 30.days,
                        )
                    }.verify {
                        expect(Coupon::value, 0.0, Greater(0.0))
                    }
                }

                it("Should fail when value is negative") {
                    shouldFailValidation<Coupon> {
                        Coupon.create(
                            code = "PROMO10",
                            discountType = "PERCENTAGE",
                            value = -10.0,
                            expiresAt = now() + 30.days,
                        )
                    }.verify {
                        expect(Coupon::value, -10.0, Greater(0.0))
                    }
                }

                it("Should fail when percentage value is greater than 100") {
                    shouldFailValidation<Coupon> {
                        Coupon.create(
                            code = "PROMO10",
                            discountType = "PERCENTAGE",
                            value = 150.0,
                            expiresAt = now() + 30.days,
                        )
                    }.verify {
                        expect(Coupon::value, 150.0, LessOrEqual(100.0))
                    }
                }
            }
        }

        describe("Checking if coupon is valid") {
            context("Valid coupon") {
                it("Should return true when not expired") {
                    coupon().isValid() shouldBe true
                }
            }

            context("Expired coupon") {
                it("Should return false when expired") {
                    expiredCoupon().isValid() shouldBe false
                }
            }
        }

        describe("Applying discount") {
            context("Percentage discount") {
                it("Should apply percentage discount correctly") {
                    val coupon = percentageDiscountCoupon(value = 20.0)
                    val amount = 100.0
                    val expected = 80.0 // 20% off

                    coupon.applyDiscount(amount) shouldBe expected
                }

                it("Should not apply discount if coupon is expired") {
                    val expired = expiredCoupon(value = 10.0)
                    val amount = 100.0

                    expired.applyDiscount(amount) shouldBe amount
                }
            }

            context("Fixed discount") {
                it("Should apply fixed discount correctly") {
                    val coupon = fixedDiscountCoupon(value = 15.0)
                    val amount = 100.0
                    val expected = 85.0 // 15 off

                    coupon.applyDiscount(amount) shouldBe expected
                }

                it("Should not go below zero") {
                    val coupon = fixedDiscountCoupon(value = 150.0)
                    val amount = 100.0
                    val expected = 0.0

                    coupon.applyDiscount(amount) shouldBe expected
                }

                it("Should not apply discount if coupon is expired") {
                    val expired =
                        expiredCoupon(
                            discountType = DiscountType.FIXED,
                            value = 10.0,
                        )
                    val amount = 100.0

                    expired.applyDiscount(amount) shouldBe amount
                }
            }
        }

        describe("Updating a Coupon") {
            context("Valid update") {
                it("Should update coupon properties") {
                    val coupon = coupon()
                    val now = now()
                    val updatedCoupon =
                        coupon.update(
                            discountType = "FIXED",
                            value = 25.0,
                            expiresAt = now + 60.days,
                        )

                    updatedCoupon.code shouldBe coupon.code
                    updatedCoupon.discountType shouldBe DiscountType.FIXED
                    updatedCoupon.value shouldBe 25.0
                    updatedCoupon.expiresAt shouldBe now + 60.days
                }
            }
        }
    }
}
