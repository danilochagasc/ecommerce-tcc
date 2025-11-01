package com.danilo.tcc.stock.core.application.category

import com.danilo.tcc.stock.core.application.category.CategoryServiceTestFixture.createCategoryCommand
import com.danilo.tcc.stock.core.application.category.CategoryServiceTestFixture.updateCategoryCommand
import com.danilo.tcc.stock.core.domain.category.CategoryAlreadyExistsException
import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.category.CategoryNotFoundException
import com.danilo.tcc.stock.core.domain.category.CategoryRepository
import com.danilo.tcc.stock.core.domain.category.CategoryTestFixture.category
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

class CategoryServiceTest : DescribeSpec() {
    private val repository = mockk<CategoryRepository>()

    private val service = CategoryService(repository = repository)

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(repository)
    }

    init {
        describe("Finding category by id") {
            context("Category exists") {
                it("Should return category") {
                    val category = category()
                    coEvery { repository.findById(category.id) } returns category

                    val result = runBlocking { service.findById(category.id) }

                    result.id shouldBe category.id.toString()
                    result.name shouldBe category.name
                    coVerify { repository.findById(category.id) }
                }
            }

            context("Category does not exist") {
                it("Should throw CategoryNotFoundException") {
                    val categoryId = CategoryId()
                    coEvery { repository.findById(categoryId) } returns null

                    shouldThrow<CategoryNotFoundException> {
                        runBlocking { service.findById(categoryId) }
                    }

                    coVerify { repository.findById(categoryId) }
                }
            }
        }

        describe("Finding all categories") {
            it("Should return all categories") {
                val category1 = category()
                val category2 = category()
                coEvery { repository.findAll() } returns listOf(category1, category2)

                val result = runBlocking { service.findAll() }

                result.size shouldBe 2
                coVerify { repository.findAll() }
            }

            it("Should return empty list when no categories exist") {
                coEvery { repository.findAll() } returns emptyList()

                val result = runBlocking { service.findAll() }

                result shouldBe emptyList()
                coVerify { repository.findAll() }
            }
        }

        describe("Creating category") {
            context("With valid data") {
                it("Should create category successfully") {
                    val command = createCategoryCommand()
                    coEvery { repository.existsByName(command.name) } returns false
                    coEvery { repository.create(any()) } returns Unit

                    val result = runBlocking { service.create(command) }

                    result shouldNotBe null
                    coVerify { repository.existsByName(command.name) }
                    coVerify { repository.create(any()) }
                }
            }

            context("When category name already exists") {
                it("Should throw CategoryAlreadyExistsException") {
                    val command = createCategoryCommand()
                    coEvery { repository.existsByName(command.name) } returns true

                    shouldThrow<CategoryAlreadyExistsException> {
                        runBlocking { service.create(command) }
                    }

                    coVerify { repository.existsByName(command.name) }
                    coVerify(exactly = 0) { repository.create(any()) }
                }
            }
        }

        describe("Updating category") {
            context("Category exists") {
                it("Should update category successfully") {
                    val category = category()
                    val command = updateCategoryCommand(id = category.id)
                    coEvery { repository.findById(category.id) } returns category
                    coEvery { repository.update(any()) } returns Unit

                    runBlocking { service.update(command) }

                    coVerify { repository.findById(category.id) }
                    coVerify { repository.update(any()) }
                }
            }

            context("Category does not exist") {
                it("Should throw CategoryNotFoundException") {
                    val command = updateCategoryCommand()
                    coEvery { repository.findById(command.id) } returns null

                    shouldThrow<CategoryNotFoundException> {
                        runBlocking { service.update(command) }
                    }

                    coVerify { repository.findById(command.id) }
                    coVerify(exactly = 0) { repository.update(any()) }
                }
            }
        }

        describe("Deleting category") {
            context("Category exists") {
                it("Should delete category successfully") {
                    val category = category()
                    coEvery { repository.findById(category.id) } returns category
                    coEvery { repository.delete(category.id) } returns Unit

                    runBlocking { service.delete(category.id) }

                    coVerify { repository.findById(category.id) }
                    coVerify { repository.delete(category.id) }
                }
            }

            context("Category does not exist") {
                it("Should throw CategoryNotFoundException") {
                    val categoryId = CategoryId()
                    coEvery { repository.findById(categoryId) } returns null

                    shouldThrow<CategoryNotFoundException> {
                        runBlocking { service.delete(categoryId) }
                    }

                    coVerify { repository.findById(categoryId) }
                    coVerify(exactly = 0) { repository.delete(any()) }
                }
            }
        }
    }
}
