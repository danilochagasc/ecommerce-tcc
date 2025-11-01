package com.danilo.tcc.stock.adapter.r2dbc

import com.danilo.tcc.stock.adapter.r2dbc.ProductR2dbcRepositoryTestFixture.product
import com.danilo.tcc.stock.adapter.r2dbc.ProductR2dbcRepositoryTestFixture.productId
import com.danilo.tcc.stock.adapter.r2dbc.ProductR2dbcRepositoryTestFixture.productUpdated
import com.danilo.tcc.stock.core.domain.product.ProductId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

private const val INSERT = "product_insert.sql"
private const val DELETE = "product_delete.sql"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProductR2dbcRepositoryTest(
    private val repository: ProductR2dbcRepository,
    private val scriptRunner: R2dbcScriptRunner,
) : DescribeSpec() {
    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> =
            PostgresTestContainer.createContainer().apply {
                // Run Flyway migrations before Spring context starts
                org.flywaydb.core.Flyway
                    .configure()
                    .dataSource(jdbcUrl, username, password)
                    .locations("classpath:db/migration")
                    .load()
                    .migrate()
            }

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            PostgresTestContainer.configureProperties(postgres, registry)
        }
    }

    override suspend fun beforeTest(testCase: TestCase) =
        run {
            scriptRunner.run(DELETE)
            scriptRunner.run(INSERT)
        }

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) = scriptRunner.run(DELETE)

    init {
        describe("Use postgres as a repository for products") {

            context("Finding product by id") {
                it("Should return product when it exists") {
                    repository.findById(productId()) shouldBe product()
                }

                it("Should return null when product does not exist") {
                    repository.findById(ProductId()) shouldBe null
                }
            }

            context("Finding all products") {
                it("Should return all products") {
                    val result = repository.findAll()

                    result.size shouldBe 2
                    result.map { it.name }.toSet() shouldBe setOf("Product 1", "Product 2")
                }
            }

            context("Checking if product exists by name") {
                it("Should return true when product exists") {
                    repository.existsByName("Product 1") shouldBe true
                }

                it("Should return false when product does not exist") {
                    repository.existsByName("Non-existent Product") shouldBe false
                }
            }

            context("Creating product") {
                it("Should create product successfully") {
                    scriptRunner.run(DELETE)
                    scriptRunner.run("category_insert.sql")

                    repository.create(product())

                    repository.findById(productId()) shouldBe product()
                }
            }

            context("Updating product") {
                it("Should update product successfully") {
                    repository.update(productUpdated())

                    repository.findById(productId()) shouldBe productUpdated()
                }
            }

            context("Deleting product") {
                it("Should delete product successfully") {
                    repository.delete(productId())

                    repository.findById(productId()) shouldBe null
                }
            }
        }
    }
}
