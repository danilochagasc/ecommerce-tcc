package com.br.danilo.tcc.order.adapter.r2dbc

import com.br.danilo.tcc.order.adapter.r2dbc.ItemR2dbcRepositoryTestFixture.item
import com.br.danilo.tcc.order.adapter.r2dbc.ItemR2dbcRepositoryTestFixture.itemId
import com.br.danilo.tcc.order.adapter.r2dbc.ItemR2dbcRepositoryTestFixture.orderId
import com.br.danilo.tcc.order.core.domain.order.OrderId
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

private const val ORDER_INSERT = "order_insert.sql"
private const val ORDER_DELETE = "order_delete.sql"
private const val ITEM_INSERT = "item_insert.sql"
private const val ITEM_DELETE = "item_delete.sql"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ItemR2dbcRepositoryTest(
    private val repository: ItemR2dbcRepository,
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
            scriptRunner.run(ITEM_DELETE)
            scriptRunner.run(ORDER_DELETE)
            scriptRunner.run(ORDER_INSERT)
            scriptRunner.run(ITEM_INSERT)
        }

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        scriptRunner.run(ITEM_DELETE)
        scriptRunner.run(ORDER_DELETE)
    }

    init {
        describe("Use postgres as a repository for items") {
            context("Finding all items") {
                it("Should return all items") {
                    val result = repository.findAll()

                    result.size shouldBe 3
                    result.map { it.name }.toSet() shouldBe setOf("Item 1", "Item 2", "Item 3")
                }
            }

            context("Finding items by order id") {
                it("Should return items for order") {
                    val result = repository.findAllByOrderId(orderId())

                    result.size shouldBe 2
                    result.map { it.name }.toSet() shouldBe setOf("Item 1", "Item 2")
                    result.forEach { it.orderId shouldBe orderId() }
                }

                it("Should return empty list when order has no items") {
                    val result = repository.findAllByOrderId(OrderId())

                    result shouldBe emptyList()
                }
            }

            context("Creating item") {
                it("Should create item successfully") {
                    scriptRunner.run(ITEM_DELETE)
                    scriptRunner.run(ORDER_DELETE)
                    scriptRunner.run(ORDER_INSERT)

                    repository.create(item())

                    val items = repository.findAllByOrderId(orderId())
                    items.any { it.id == itemId() } shouldBe true
                    items.find { it.id == itemId() }?.name shouldBe "Item 1"
                    items.find { it.id == itemId() }?.price shouldBe 50.0
                    items.find { it.id == itemId() }?.quantity shouldBe 2
                }
            }
        }
    }
}

