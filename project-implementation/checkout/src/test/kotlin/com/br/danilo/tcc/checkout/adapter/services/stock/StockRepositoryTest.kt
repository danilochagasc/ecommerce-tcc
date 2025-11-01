package com.br.danilo.tcc.checkout.adapter.services.stock

import com.br.danilo.tcc.checkout.adapter.services.stock.StockRepositoryTestFixture.product
import com.br.danilo.tcc.checkout.adapter.services.stock.StockRepositoryTestFixture.productJson
import com.br.danilo.tcc.checkout.core.domain.cart.ProductId
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.put
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.Options
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.builder

class StockRepositoryTest : DescribeSpec() {
    private val wiremock = WireMockServer(Options.DYNAMIC_PORT)

    private lateinit var stockRepository: StockRepository
    private lateinit var stockWebClient: WebClient

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun beforeTest(testCase: TestCase) {
        wiremock.start()

        val json =
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }

        stockWebClient =
            builder()
                .baseUrl("http://localhost:${wiremock.port()}")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build()

        stockRepository = StockRepository(stockWebClient)
    }

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        wiremock.stop()
    }

    init {
        describe("Stock Service Tests") {

            describe("Finding product by id") {

                context("Product exists") {
                    it("Should fetch product from stock service") {
                        val productId = ProductId()
                        val product = product(id = productId.toString())

                        wiremock.stubFor(
                            get(urlEqualTo("/$productId"))
                                .willReturn(
                                    aResponse()
                                        .withStatus(OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(productJson(product)),
                                ),
                        )

                        val result = stockRepository.findById(productId)

                        result shouldNotBe null
                        result?.productId shouldBe productId
                        result?.name shouldBe product.name
                        result?.price shouldBe product.price
                        result?.quantity shouldBe product.quantity
                    }
                }

                context("Stock service returns error") {
                    it("Should throw exception when service returns internal error") {
                        val productId = ProductId()

                        wiremock.stubFor(
                            get(urlEqualTo("/$productId"))
                                .willReturn(
                                    aResponse()
                                        .withStatus(INTERNAL_SERVER_ERROR.value()),
                                ),
                        )

                        shouldThrow<Exception> {
                            stockRepository.findById(productId)
                        }
                    }
                }
            }

            describe("Decreasing product quantity") {

                context("Valid request") {
                    it("Should decrease quantity successfully") {
                        val productId = ProductId()
                        val amount = 5

                        wiremock.stubFor(
                            put(urlEqualTo("/$productId/decrease/$amount"))
                                .willReturn(
                                    aResponse()
                                        .withStatus(HttpStatus.NO_CONTENT.value()),
                                ),
                        )

                        stockRepository.decreaseQuantity(productId, amount) shouldBe Unit
                    }
                }

                context("Product does not exist") {
                    it("Should throw exception when product not found") {
                        val productId = ProductId()
                        val amount = 5

                        wiremock.stubFor(
                            put(urlEqualTo("/$productId/decrease/$amount"))
                                .willReturn(
                                    aResponse()
                                        .withStatus(NOT_FOUND.value()),
                                ),
                        )

                        shouldThrow<Exception> {
                            stockRepository.decreaseQuantity(productId, amount)
                        }
                    }
                }

                context("Stock service returns error") {
                    it("Should throw exception when service returns internal error") {
                        val productId = ProductId()
                        val amount = 5

                        wiremock.stubFor(
                            put(urlEqualTo("/$productId/decrease/$amount"))
                                .willReturn(
                                    aResponse()
                                        .withStatus(INTERNAL_SERVER_ERROR.value()),
                                ),
                        )

                        shouldThrow<Exception> {
                            stockRepository.decreaseQuantity(productId, amount)
                        }
                    }
                }
            }
        }
    }
}
