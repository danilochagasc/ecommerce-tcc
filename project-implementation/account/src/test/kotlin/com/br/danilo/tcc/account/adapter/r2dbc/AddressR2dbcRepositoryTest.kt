package com.br.danilo.tcc.account.adapter.r2dbc

import com.br.danilo.tcc.account.adapter.r2dbc.AddressR2dbcRepositoryTestFixture.address
import com.br.danilo.tcc.account.adapter.r2dbc.AddressR2dbcRepositoryTestFixture.addressId
import com.br.danilo.tcc.account.adapter.r2dbc.AddressR2dbcRepositoryTestFixture.anotherAddress
import com.br.danilo.tcc.account.adapter.r2dbc.AddressR2dbcRepositoryTestFixture.anotherAddressId
import com.br.danilo.tcc.account.adapter.r2dbc.AddressR2dbcRepositoryTestFixture.userId
import com.br.danilo.tcc.account.core.domain.address.AddressId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

private const val INSERT = "address_insert.sql"
private const val DELETE = "address_delete.sql"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AddressR2dbcRepositoryTest(
    private val repository: AddressR2dbcRepository,
    private val scriptRunner: R2dbcScriptRunner,
) : DescribeSpec() {

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgresTestContainer.createContainer().apply {
            org.flywaydb.core.Flyway.configure()
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

    override suspend fun beforeTest(testCase: TestCase) = runBlocking {
        scriptRunner.run(DELETE)
        scriptRunner.run(INSERT)
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) = runBlocking {
        scriptRunner.run(DELETE)
    }

    init {
        describe("Use postgres as a repository for addresses") {
            context("Finding address by id") {
                it("Should return address when it exists") {
                    repository.findById(addressId()) shouldBe address()
                }

                it("Should return null when address does not exist") {
                    repository.findById(AddressId()) shouldBe null
                }
            }

            context("Finding all addresses by user id") {
                it("Should return all addresses for user") {
                    val result = repository.findAllByUserId(userId())

                    result.size shouldBe 2
                    result.map { it.street }.toSet() shouldBe setOf("Main Street", "Second Street")
                }
            }

            context("Creating address") {
                it("Should create address successfully") {
                    scriptRunner.run(DELETE)
                    scriptRunner.run("user_insert.sql")

                    repository.create(address())

                    val result = repository.findById(addressId())
                    result shouldBe address()
                }
            }

            context("Updating address") {
                it("Should update address successfully") {
                    val updated = address().copy(
                        street = "Updated Street",
                        number = "999",
                    )

                    repository.update(updated)

                    repository.findById(addressId())?.street shouldBe "Updated Street"
                    repository.findById(addressId())?.number shouldBe "999"
                }
            }

            context("Deleting address") {
                it("Should delete address successfully") {
                    repository.delete(addressId())

                    repository.findById(addressId()) shouldBe null
                }
            }

            context("Deleting all addresses by user id") {
                it("Should delete all addresses for user") {
                    repository.deleteAllByUserId(userId())

                    repository.findAllByUserId(userId()).size shouldBe 0
                }
            }
        }
    }
}

