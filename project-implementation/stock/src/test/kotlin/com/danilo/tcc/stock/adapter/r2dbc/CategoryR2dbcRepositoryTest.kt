package com.danilo.tcc.stock.adapter.r2dbc

import com.danilo.tcc.stock.adapter.r2dbc.CategoryR2dbcRepositoryTestFixture.category
import com.danilo.tcc.stock.adapter.r2dbc.CategoryR2dbcRepositoryTestFixture.categoryId
import com.danilo.tcc.stock.adapter.r2dbc.CategoryR2dbcRepositoryTestFixture.categoryUpdated
import com.danilo.tcc.stock.core.domain.category.CategoryId
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

private const val INSERT = "category_insert.sql"
private const val DELETE = "category_delete.sql"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CategoryR2dbcRepositoryTest(
    private val repository: CategoryR2dbcRepository,
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
        describe("Use postgres as a repository for categories") {

            context("Finding category by id") {
                it("Should return category when it exists") {
                    repository.findById(categoryId()) shouldBe category()
                }

                it("Should return null when category does not exist") {
                    repository.findById(CategoryId()) shouldBe null
                }
            }

            context("Finding all categories") {
                it("Should return all categories") {
                    val result = repository.findAll()

                    result.size shouldBe 2
                    result.map { it.name }.toSet() shouldBe setOf("Category 1", "Category 2")
                }
            }

            context("Checking if category exists by name") {
                it("Should return true when category exists") {
                    repository.existsByName("Category 1") shouldBe true
                }

                it("Should return false when category does not exist") {
                    repository.existsByName("Non-existent Category") shouldBe false
                }
            }

            context("Creating category") {
                it("Should create category successfully") {
                    scriptRunner.run(DELETE)

                    repository.create(category())

                    repository.findById(categoryId()) shouldBe category()
                }
            }

            context("Updating category") {
                it("Should update category successfully") {
                    repository.update(categoryUpdated())

                    repository.findById(categoryId()) shouldBe categoryUpdated()
                }
            }

            context("Deleting category") {
                it("Should delete category successfully") {
                    repository.delete(categoryId())

                    repository.findById(categoryId()) shouldBe null
                }
            }
        }
    }
}
