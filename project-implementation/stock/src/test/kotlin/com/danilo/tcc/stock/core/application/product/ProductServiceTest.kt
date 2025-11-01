package com.danilo.tcc.stock.core.application.product

import com.danilo.tcc.stock.core.application.product.ProductServiceTestFixture.createProductCommand
import com.danilo.tcc.stock.core.application.product.ProductServiceTestFixture.decreaseQuantityCommand
import com.danilo.tcc.stock.core.application.product.ProductServiceTestFixture.updateProductCommand
import com.danilo.tcc.stock.core.domain.product.ProductAlreadyExistsException
import com.danilo.tcc.stock.core.domain.product.ProductId
import com.danilo.tcc.stock.core.domain.product.ProductImageRepository
import com.danilo.tcc.stock.core.domain.product.ProductNotFoundException
import com.danilo.tcc.stock.core.domain.product.ProductRepository
import com.danilo.tcc.stock.core.domain.product.ProductTestFixture.product
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

class ProductServiceTest : DescribeSpec() {
    private val repository = mockk<ProductRepository>()
    private val imageRepository = mockk<ProductImageRepository>()

    private val service =
        ProductService(
            repository = repository,
            imageRepository = imageRepository,
        )

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(repository, imageRepository)
    }

    init {
        describe("Finding product by id") {
            context("Product exists") {
                it("Should return product") {
                    val product = product()
                    coEvery { repository.findById(product.id) } returns product

                    val result = runBlocking { service.findById(product.id) }

                    result.id shouldBe product.id.toString()
                    result.name shouldBe product.name
                    result.description shouldBe product.description
                    coVerify { repository.findById(product.id) }
                }
            }

            context("Product does not exist") {
                it("Should throw ProductNotFoundException") {
                    val productId = ProductId()
                    coEvery { repository.findById(productId) } returns null

                    shouldThrow<ProductNotFoundException> {
                        runBlocking { service.findById(productId) }
                    }

                    coVerify { repository.findById(productId) }
                }
            }
        }

        describe("Finding all products") {
            it("Should return all products") {
                val product1 = product()
                val product2 = product()
                coEvery { repository.findAll() } returns listOf(product1, product2)

                val result = runBlocking { service.findAll() }

                result.size shouldBe 2
                coVerify { repository.findAll() }
            }

            it("Should return empty list when no products exist") {
                coEvery { repository.findAll() } returns emptyList()

                val result = runBlocking { service.findAll() }

                result shouldBe emptyList()
                coVerify { repository.findAll() }
            }
        }

        describe("Creating product") {
            context("With valid data") {
                it("Should create product successfully") {
                    val command = createProductCommand()
                    coEvery { repository.existsByName(command.name) } returns false
                    coEvery { repository.create(any()) } returns Unit

                    val result = runBlocking { service.create(command) }

                    result shouldNotBe null
                    coVerify { repository.existsByName(command.name) }
                    coVerify { repository.create(any()) }
                }
            }

            context("When product name already exists") {
                it("Should throw ProductAlreadyExistsException") {
                    val command = createProductCommand()
                    coEvery { repository.existsByName(command.name) } returns true

                    shouldThrow<ProductAlreadyExistsException> {
                        runBlocking { service.create(command) }
                    }

                    coVerify { repository.existsByName(command.name) }
                    coVerify(exactly = 0) { repository.create(any()) }
                }
            }
        }

        describe("Updating product") {
            context("Product exists") {
                it("Should update product successfully") {
                    val product = product()
                    val command = updateProductCommand(id = product.id)
                    coEvery { repository.findById(product.id) } returns product
                    coEvery { repository.update(any()) } returns Unit

                    runBlocking { service.update(command) }

                    coVerify { repository.findById(product.id) }
                    coVerify { repository.update(any()) }
                }
            }

            context("Product does not exist") {
                it("Should throw ProductNotFoundException") {
                    val command = updateProductCommand()
                    coEvery { repository.findById(command.id) } returns null

                    shouldThrow<ProductNotFoundException> {
                        runBlocking { service.update(command) }
                    }

                    coVerify { repository.findById(command.id) }
                    coVerify(exactly = 0) { repository.update(any()) }
                }
            }
        }

        describe("Decreasing product quantity") {
            context("Product exists with sufficient quantity") {
                it("Should decrease quantity successfully") {
                    val product = product(quantity = 100)
                    val command = decreaseQuantityCommand(productId = product.id, amount = 30)
                    coEvery { repository.findById(product.id) } returns product
                    coEvery { repository.update(any()) } returns Unit

                    runBlocking { service.decreaseQuantity(command) }

                    coVerify { repository.findById(product.id) }
                    coVerify { repository.update(any()) }
                }
            }

            context("Product does not exist") {
                it("Should throw ProductNotFoundException") {
                    val command = decreaseQuantityCommand()
                    coEvery { repository.findById(command.productId) } returns null

                    shouldThrow<ProductNotFoundException> {
                        runBlocking { service.decreaseQuantity(command) }
                    }

                    coVerify { repository.findById(command.productId) }
                    coVerify(exactly = 0) { repository.update(any()) }
                }
            }
        }

        describe("Uploading product image") {
            context("Product exists") {
                it("Should upload image successfully") {
                    val product = product()
                    val productId = product.id
                    val bytes = "image content".toByteArray()
                    val fileName = "image.jpg"
                    val contentType = "image/jpeg"
                    val imageUrl = "https://example.com/image.jpg"

                    coEvery { imageRepository.uploadImage(productId, bytes, fileName, contentType) } returns imageUrl
                    coEvery { repository.findById(productId) } returns product
                    coEvery { repository.update(any()) } returns Unit

                    runBlocking {
                        service.uploadImage(productId, bytes, fileName, contentType)
                    }

                    coVerify { imageRepository.uploadImage(productId, bytes, fileName, contentType) }
                    coVerify { repository.findById(productId) }
                    coVerify { repository.update(any()) }
                }
            }

            context("Product does not exist") {
                it("Should throw ProductNotFoundException") {
                    val productId = ProductId()
                    val bytes = "image content".toByteArray()
                    val fileName = "image.jpg"
                    val contentType = "image/jpeg"

                    coEvery { imageRepository.uploadImage(productId, bytes, fileName, contentType) } returns "https://example.com/image.jpg"
                    coEvery { repository.findById(productId) } returns null

                    shouldThrow<ProductNotFoundException> {
                        runBlocking {
                            service.uploadImage(productId, bytes, fileName, contentType)
                        }
                    }

                    coVerify { imageRepository.uploadImage(productId, bytes, fileName, contentType) }
                    coVerify { repository.findById(productId) }
                    coVerify(exactly = 0) { repository.update(any()) }
                }
            }
        }

        describe("Deleting product") {
            context("Product exists") {
                it("Should delete product successfully") {
                    val product = product()
                    coEvery { repository.findById(product.id) } returns product
                    coEvery { repository.delete(product.id) } returns Unit

                    runBlocking { service.delete(product.id) }

                    coVerify { repository.findById(product.id) }
                    coVerify { repository.delete(product.id) }
                }
            }

            context("Product does not exist") {
                it("Should throw ProductNotFoundException") {
                    val productId = ProductId()
                    coEvery { repository.findById(productId) } returns null

                    shouldThrow<ProductNotFoundException> {
                        runBlocking { service.delete(productId) }
                    }

                    coVerify { repository.findById(productId) }
                    coVerify(exactly = 0) { repository.delete(any()) }
                }
            }
        }
    }
}
