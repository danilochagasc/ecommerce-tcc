package com.danilo.tcc.stock.adapter.http.handler

import com.danilo.tcc.stock.adapter.http.handler.CategoryHandlerTestFixture.anotherCategoryQuery
import com.danilo.tcc.stock.adapter.http.handler.CategoryHandlerTestFixture.categoryQuery
import com.danilo.tcc.stock.adapter.router.createWebTestClient
import com.danilo.tcc.stock.core.application.category.CategoryService
import com.danilo.tcc.stock.core.application.category.CategoryServiceTestFixture.createCategoryCommand
import com.danilo.tcc.stock.core.application.category.query.CategoryQuery
import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.category.CategoryNotFoundException
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
class CategoryHandlerTest(
    applicationContext: ApplicationContext,
) : DescribeSpec() {
    @MockkBean
    private lateinit var categoryService: CategoryService

    private val webClient = createWebTestClient(applicationContext)

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(categoryService)
    }

    init {
        describe("Finding category by id") {
            context("Category does not exist") {
                it("Should return 404 when category does not exist") {
                    val categoryId = CategoryId()
                    coEvery { categoryService.findById(any()) } throws CategoryNotFoundException(categoryId)

                    webClient
                        .get()
                        .uri("/category/$categoryId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isNotFound

                    coVerify { categoryService.findById(eq(categoryId)) }
                    confirmVerified(categoryService)
                }
            }

            context("Category exists") {
                it("Should find category and return 200") {
                    val categoryQuery = categoryQuery()
                    val categoryId = CategoryId(categoryQuery.id)

                    coEvery { categoryService.findById(eq(categoryId)) } returns categoryQuery

                    webClient
                        .get()
                        .uri("/category/$categoryId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<CategoryQuery>()
                        .isEqualTo(categoryQuery)

                    coVerify { categoryService.findById(eq(categoryId)) }
                    confirmVerified(categoryService)
                }
            }
        }

        describe("Finding all categories") {
            it("Should return all categories") {
                val categories = listOf(categoryQuery(), anotherCategoryQuery())
                coEvery { categoryService.findAll() } returns categories

                webClient
                    .get()
                    .uri("/category")
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(APPLICATION_JSON)
                    .expectBody<List<CategoryQuery>>()

                coVerify { categoryService.findAll() }
                confirmVerified(categoryService)
            }
        }

        describe("Creating category") {
            it("Should create category and return 201") {
                val command = createCategoryCommand()
                val categoryId = CategoryId()

                coEvery { categoryService.create(any()) } returns categoryId

                webClient
                    .post()
                    .uri("/category")
                    .contentType(APPLICATION_JSON)
                    .bodyValue(command)
                    .exchange()
                    .expectStatus()
                    .isCreated

                coVerify { categoryService.create(any()) }
                confirmVerified(categoryService)
            }
        }

        describe("Updating category") {
            it("Should update category and return 200") {
                val categoryId = CategoryId()
                val updateRequest =
                    com.danilo.tcc.stock.adapter.http.request.UpdateCategoryRequest(
                        name = "Updated Category",
                    )

                coEvery { categoryService.update(any()) } returns Unit

                webClient
                    .put()
                    .uri("/category/$categoryId")
                    .contentType(APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .exchange()
                    .expectStatus()
                    .isOk

                coVerify { categoryService.update(any()) }
                confirmVerified(categoryService)
            }
        }

        describe("Deleting category") {
            it("Should delete category and return 204") {
                val categoryId = CategoryId()

                coEvery { categoryService.delete(any()) } returns Unit

                webClient
                    .delete()
                    .uri("/category/$categoryId")
                    .exchange()
                    .expectStatus()
                    .isNoContent
                    .expectBody()
                    .isEmpty

                coVerify { categoryService.delete(eq(categoryId)) }
                confirmVerified(categoryService)
            }
        }
    }
}
