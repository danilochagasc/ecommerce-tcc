package com.danilo.tcc.stock.adapter.http.handler

import com.danilo.tcc.stock.adapter.http.handler.ProductHandlerTestFixture.anotherProductQuery
import com.danilo.tcc.stock.adapter.http.handler.ProductHandlerTestFixture.productQuery
import com.danilo.tcc.stock.adapter.router.createWebTestClient
import com.danilo.tcc.stock.core.application.product.ProductService
import com.danilo.tcc.stock.core.application.product.ProductServiceTestFixture.createProductCommand
import com.danilo.tcc.stock.core.application.product.query.ProductQuery
import com.danilo.tcc.stock.core.domain.product.ProductId
import com.danilo.tcc.stock.core.domain.product.ProductNotFoundException
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
class ProductHandlerTest(
    applicationContext: ApplicationContext,
) : DescribeSpec() {
    @MockkBean
    private lateinit var productService: ProductService

    private val webClient = createWebTestClient(applicationContext)

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(productService)
    }

    init {
        describe("Finding product by id") {
            context("Product does not exist") {
                it("Should return 404 when product does not exist") {
                    val productId = ProductId()
                    coEvery { productService.findById(any()) } throws ProductNotFoundException(productId)

                    webClient
                        .get()
                        .uri("/product/$productId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isNotFound

                    coVerify { productService.findById(eq(productId)) }
                    confirmVerified(productService)
                }
            }

            context("Product exists") {
                it("Should find product and return 200") {
                    val productQuery = productQuery()
                    val productId = ProductId(productQuery.id)

                    coEvery { productService.findById(eq(productId)) } returns productQuery

                    webClient
                        .get()
                        .uri("/product/$productId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<ProductQuery>()
                        .isEqualTo(productQuery)

                    coVerify { productService.findById(eq(productId)) }
                    confirmVerified(productService)
                }
            }
        }

        describe("Finding all products") {
            it("Should return all products") {
                val products = listOf(productQuery(), anotherProductQuery())
                coEvery { productService.findAll() } returns products

                webClient
                    .get()
                    .uri("/product")
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(APPLICATION_JSON)
                    .expectBody<List<ProductQuery>>()

                coVerify { productService.findAll() }
                confirmVerified(productService)
            }
        }

        describe("Creating product") {
            it("Should create product and return 201") {
                val command = createProductCommand()
                val productId = ProductId()

                coEvery { productService.create(any()) } returns productId

                webClient
                    .post()
                    .uri("/product")
                    .contentType(APPLICATION_JSON)
                    .bodyValue(command)
                    .exchange()
                    .expectStatus()
                    .isCreated

                coVerify { productService.create(any()) }
                confirmVerified(productService)
            }
        }

        describe("Updating product") {
            it("Should update product and return 200") {
                val productId = ProductId()
                val updateRequest =
                    com.danilo.tcc.stock.adapter.http.request.UpdateProductRequest(
                        name = "Updated Product",
                        description = "Updated description",
                        price = 15.0,
                        quantity = 150,
                        categoryId =
                            com.danilo.tcc.stock.core.domain.category
                                .CategoryId(),
                    )

                coEvery { productService.update(any()) } returns Unit

                webClient
                    .put()
                    .uri("/product/$productId")
                    .contentType(APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .exchange()
                    .expectStatus()
                    .isOk

                coVerify { productService.update(any()) }
                confirmVerified(productService)
            }
        }

        describe("Decreasing product quantity") {
            it("Should decrease quantity and return 200") {
                val productId = ProductId()
                val amount = 10

                coEvery { productService.decreaseQuantity(any()) } returns Unit

                webClient
                    .put()
                    .uri("/product/$productId/decrease/$amount")
                    .exchange()
                    .expectStatus()
                    .isOk

                coVerify { productService.decreaseQuantity(any()) }
                confirmVerified(productService)
            }
        }

        describe("Deleting product") {
            it("Should delete product and return 204") {
                val productId = ProductId()

                coEvery { productService.delete(any()) } returns Unit

                webClient
                    .delete()
                    .uri("/product/$productId")
                    .exchange()
                    .expectStatus()
                    .isNoContent
                    .expectBody()
                    .isEmpty

                coVerify { productService.delete(eq(productId)) }
                confirmVerified(productService)
            }
        }
    }
}
