package com.br.danilo.tcc.checkout.adapter.http.handler

import com.br.danilo.tcc.checkout.adapter.http.handler.CouponHandlerTestFixture.couponQueries
import com.br.danilo.tcc.checkout.adapter.http.handler.CouponHandlerTestFixture.couponQuery
import com.br.danilo.tcc.checkout.adapter.http.handler.CouponHandlerTestFixture.createCouponRequest
import com.br.danilo.tcc.checkout.adapter.http.handler.CouponHandlerTestFixture.updateCouponRequest
import com.br.danilo.tcc.checkout.adapter.router.createWebTestClient
import com.br.danilo.tcc.checkout.core.application.coupon.CouponService
import com.br.danilo.tcc.checkout.core.application.coupon.query.CouponQuery
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponNotFoundException
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
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest
class CouponHandlerTest(
    applicationContext: ApplicationContext,
) : DescribeSpec() {
    @MockkBean
    private lateinit var couponService: CouponService

    private val webClient = createWebTestClient(applicationContext)

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(couponService)
    }

    init {
        describe("Finding coupon by code") {

            context("Coupon does not exist") {
                it("Should return 404 when coupon does not exist") {
                    val code = "INVALID"

                    coEvery { couponService.findByCode(eq(code.uppercase())) } throws CouponNotFoundException(code)

                    webClient
                        .get()
                        .uri("/coupon/$code")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isNotFound

                    coVerify { couponService.findByCode(eq(code.uppercase())) }
                    confirmVerified(couponService)
                }
            }

            context("Coupon exists") {
                it("Should find coupon and return 200") {
                    val couponQuery = couponQuery()
                    val code = couponQuery.code

                    coEvery { couponService.findByCode(eq(code)) } returns couponQuery

                    webClient
                        .get()
                        .uri("/coupon/$code")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<CouponQuery>()
                        .isEqualTo(couponQuery)

                    coVerify { couponService.findByCode(eq(code)) }
                    confirmVerified(couponService)
                }

                it("Should convert code to uppercase") {
                    val couponQuery = couponQuery()
                    val codeLowercase = couponQuery.code.lowercase()

                    coEvery { couponService.findByCode(eq(couponQuery.code)) } returns couponQuery

                    webClient
                        .get()
                        .uri("/coupon/$codeLowercase")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<CouponQuery>()
                        .isEqualTo(couponQuery)

                    coVerify { couponService.findByCode(eq(couponQuery.code)) }
                    confirmVerified(couponService)
                }
            }
        }

        describe("Finding all coupons") {

            context("No coupons exist") {
                it("Should return empty list") {
                    coEvery { couponService.findAll() } returns emptyList()

                    webClient
                        .get()
                        .uri("/coupon")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<List<CouponQuery>>()
                        .isEqualTo(emptyList())

                    coVerify { couponService.findAll() }
                    confirmVerified(couponService)
                }
            }

            context("Coupons exist") {
                it("Should return all coupons") {
                    val coupons = couponQueries()

                    coEvery { couponService.findAll() } returns coupons

                    webClient
                        .get()
                        .uri("/coupon")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<List<CouponQuery>>()
                        .isEqualTo(coupons)

                    coVerify { couponService.findAll() }
                    confirmVerified(couponService)
                }
            }
        }

        describe("Creating coupon") {

            context("Valid request") {
                it("Should create coupon and return 201") {
                    val request = createCouponRequest()
                    val code = request.code.uppercase()

                    coEvery { couponService.create(any()) } returns code

                    webClient
                        .post()
                        .uri("/coupon")
                        .contentType(APPLICATION_JSON)
                        .bodyValue(request)
                        .exchange()
                        .expectStatus()
                        .isCreated
                        .expectHeader()
                        .valueEquals(HttpHeaders.LOCATION, "/coupon/$code")
                        .expectBody()
                        .isEmpty

                    coVerify(exactly = 1) { couponService.create(any()) }
                    confirmVerified(couponService)
                }
            }
        }

        describe("Updating coupon") {

            context("Coupon does not exist") {
                it("Should return 404 when coupon does not exist") {
                    val code = "INVALID"
                    val request = updateCouponRequest()

                    coEvery { couponService.update(any()) } throws CouponNotFoundException(code)

                    webClient
                        .put()
                        .uri("/coupon/$code")
                        .contentType(APPLICATION_JSON)
                        .bodyValue(request)
                        .exchange()
                        .expectStatus()
                        .isNotFound

                    coVerify(exactly = 1) { couponService.update(any()) }
                    confirmVerified(couponService)
                }
            }

            context("Valid request") {
                it("Should update coupon and return 204") {
                    val code = "PROMO10"
                    val request = updateCouponRequest()

                    coEvery { couponService.update(any()) } returns Unit

                    webClient
                        .put()
                        .uri("/coupon/$code")
                        .contentType(APPLICATION_JSON)
                        .bodyValue(request)
                        .exchange()
                        .expectStatus()
                        .isNoContent
                        .expectBody()
                        .isEmpty

                    coVerify(exactly = 1) { couponService.update(any()) }
                    confirmVerified(couponService)
                }

                it("Should convert code to uppercase") {
                    val codeLowercase = "promo10"
                    val request = updateCouponRequest()

                    coEvery { couponService.update(any()) } returns Unit

                    webClient
                        .put()
                        .uri("/coupon/$codeLowercase")
                        .contentType(APPLICATION_JSON)
                        .bodyValue(request)
                        .exchange()
                        .expectStatus()
                        .isNoContent

                    coVerify(exactly = 1) { couponService.update(any()) }
                    confirmVerified(couponService)
                }
            }
        }

        describe("Deleting coupon") {

            context("Coupon does not exist") {
                it("Should return 404 when coupon does not exist") {
                    val code = "INVALID"

                    coEvery { couponService.delete(eq(code.uppercase())) } throws CouponNotFoundException(code)

                    webClient
                        .delete()
                        .uri("/coupon/$code")
                        .exchange()
                        .expectStatus()
                        .isNotFound

                    coVerify { couponService.delete(eq(code.uppercase())) }
                    confirmVerified(couponService)
                }
            }

            context("Valid request") {
                it("Should delete coupon and return 204") {
                    val code = "PROMO10"

                    coEvery { couponService.delete(eq(code)) } returns Unit

                    webClient
                        .delete()
                        .uri("/coupon/$code")
                        .exchange()
                        .expectStatus()
                        .isNoContent
                        .expectBody()
                        .isEmpty

                    coVerify { couponService.delete(eq(code)) }
                    confirmVerified(couponService)
                }

                it("Should convert code to uppercase") {
                    val codeLowercase = "promo10"

                    coEvery { couponService.delete(eq(codeLowercase.uppercase())) } returns Unit

                    webClient
                        .delete()
                        .uri("/coupon/$codeLowercase")
                        .exchange()
                        .expectStatus()
                        .isNoContent

                    coVerify { couponService.delete(eq(codeLowercase.uppercase())) }
                    confirmVerified(couponService)
                }
            }
        }
    }
}
