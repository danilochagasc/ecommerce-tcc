package com.br.danilo.tcc.order.adapter.r2dbc

import com.br.danilo.tcc.order.adapter.r2dbc.OrderR2dbcRepositoryTestFixture.accountId
import com.br.danilo.tcc.order.adapter.r2dbc.OrderR2dbcRepositoryTestFixture.order
import com.br.danilo.tcc.order.adapter.r2dbc.OrderR2dbcRepositoryTestFixture.orderId
import com.br.danilo.tcc.order.adapter.r2dbc.OrderR2dbcRepositoryTestFixture.orderUpdated
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

private const val INSERT = "order_insert.sql"
private const val DELETE = "order_delete.sql"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderR2dbcRepositoryTest(
    private val repository: OrderR2dbcRepository,
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
        describe("Use postgres as a repository for orders") {
            context("Finding order by id") {
                it("Should return order when it exists") {
                    val result = repository.findById(orderId())
                    result?.id shouldBe orderId()
                    result?.accountId shouldBe accountId()
                    result?.total shouldBe 150.0
                    result?.coupon shouldBe "DISCOUNT10"
                    result?.status?.name shouldBe "CREATED"
                }

                it("Should return null when order does not exist") {
                    repository.findById(OrderId()) shouldBe null
                }
            }

            context("Finding all orders") {
                it("Should return all orders") {
                    val result = repository.findAll()

                    result.size shouldBe 2
                    result.map { it.id.toString() }.toSet() shouldBe setOf(
                        orderId().toString(),
                        "660e8400-e29b-41d4-a716-446655440001",
                    )
                }
            }

            context("Finding orders by account id") {
                it("Should return orders for account") {
                    val result = repository.finAllByAccountId(accountId())

                    result.size shouldBe 1
                    result.first().id shouldBe orderId()
                    result.first().accountId shouldBe accountId()
                }
            }

            context("Creating order") {
                it("Should create order successfully") {
                    scriptRunner.run(DELETE)

                    repository.create(order())

                    repository.findById(orderId())?.id shouldBe orderId()
                    repository.findById(orderId())?.accountId shouldBe accountId()
                }
            }

            context("Updating order") {
                it("Should update order successfully") {
                    repository.update(orderUpdated())

                    val updated = repository.findById(orderId())
                    updated?.status?.name shouldBe "PENDING_PAYMENT"
                    updated?.coupon shouldBe "NEWDISCOUNT"
                    updated?.total shouldBe 180.0
                }
            }

            context("Deleting order") {
                it("Should delete order successfully") {
                    repository.delete(orderId())

                    repository.findById(orderId()) shouldBe null
                }
            }
        }
    }
}

