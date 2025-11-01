package com.br.danilo.tcc.account.adapter.r2dbc

import com.br.danilo.tcc.account.adapter.r2dbc.UserR2dbcRepositoryTestFixture.anotherUser
import com.br.danilo.tcc.account.adapter.r2dbc.UserR2dbcRepositoryTestFixture.anotherUserId
import com.br.danilo.tcc.account.adapter.r2dbc.UserR2dbcRepositoryTestFixture.user
import com.br.danilo.tcc.account.adapter.r2dbc.UserR2dbcRepositoryTestFixture.userId
import com.br.danilo.tcc.account.core.domain.user.UserId
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

private const val INSERT = "user_insert.sql"
private const val DELETE = "user_delete.sql"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserR2dbcRepositoryTest(
    private val repository: UserR2dbcRepository,
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
        describe("Use postgres as a repository for users") {
            context("Finding user by id") {
                it("Should return user when it exists") {
                    repository.findById(userId()) shouldBe user()
                }

                it("Should return null when user does not exist") {
                    repository.findById(UserId()) shouldBe null
                }
            }

            context("Finding all users") {
                it("Should return all users") {
                    val result = repository.findAll()

                    result.size shouldBe 2
                    result.map { it.name }.toSet() shouldBe setOf("John", "Jane")
                }
            }

            context("Finding user by email") {
                it("Should return user when it exists") {
                    repository.findByEmail("john.doe@example.com") shouldBe user()
                }

                it("Should return null when user does not exist") {
                    repository.findByEmail("nonexistent@example.com") shouldBe null
                }
            }

            context("Creating user") {
                it("Should create user successfully") {
                    scriptRunner.run(DELETE)

                    repository.create(user())

                    repository.findById(userId()) shouldBe user()
                }
            }

            context("Updating user") {
                it("Should update user successfully") {
                    val updated = user().copy(
                        name = "Updated Name",
                        lastName = "Updated LastName",
                    )

                    repository.update(updated)

                    repository.findById(userId())?.name shouldBe "Updated Name"
                    repository.findById(userId())?.lastName shouldBe "Updated LastName"
                }
            }

            context("Deleting user") {
                it("Should delete user successfully") {
                    repository.delete(userId())

                    repository.findById(userId()) shouldBe null
                }
            }
        }
    }
}

