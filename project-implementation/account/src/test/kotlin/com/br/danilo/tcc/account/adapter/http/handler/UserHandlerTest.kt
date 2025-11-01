package com.br.danilo.tcc.account.adapter.http.handler

import com.br.danilo.tcc.account.adapter.http.handler.UserHandlerTestFixture.anotherUserPublicQuery
import com.br.danilo.tcc.account.adapter.http.handler.UserHandlerTestFixture.userPublicQuery
import com.br.danilo.tcc.account.adapter.router.createWebTestClient
import com.br.danilo.tcc.account.core.application.user.UserService
import com.br.danilo.tcc.account.core.application.user.command.CreateUserCommand
import com.br.danilo.tcc.account.core.application.user.query.UserPrivateQuery
import com.br.danilo.tcc.account.core.application.user.query.UserPublicQuery
import com.br.danilo.tcc.account.core.domain.user.Role
import com.br.danilo.tcc.account.core.domain.user.UserId
import com.br.danilo.tcc.account.core.domain.user.UserNotFoundException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import com.br.danilo.tcc.account.adapter.auth.TestSecurityConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest
@Import(TestSecurityConfig::class)
class UserHandlerTest(
    applicationContext: ApplicationContext,
) : DescribeSpec() {
    @MockkBean
    private lateinit var userService: UserService

    private val webClient = createWebTestClient(applicationContext)

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(userService)
    }

    init {
        describe("Finding all users") {
            it("Should return all users and return 200") {
                val users = listOf(
                    UserPrivateQuery(
                        id = userPublicQuery().id,
                        name = userPublicQuery().name,
                        lastName = userPublicQuery().lastName,
                        cpf = userPublicQuery().cpf,
                        email = userPublicQuery().email,
                        password = "hashed",
                        phone = userPublicQuery().phone,
                        birth = userPublicQuery().birth,
                        role = Role.USER,
                    ),
                    UserPrivateQuery(
                        id = anotherUserPublicQuery().id,
                        name = anotherUserPublicQuery().name,
                        lastName = anotherUserPublicQuery().lastName,
                        cpf = anotherUserPublicQuery().cpf,
                        email = anotherUserPublicQuery().email,
                        password = "hashed",
                        phone = anotherUserPublicQuery().phone,
                        birth = anotherUserPublicQuery().birth,
                        role = Role.USER,
                    ),
                )

                coEvery { userService.findAll() } returns users

                webClient
                    .get()
                    .uri("/user")
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(APPLICATION_JSON)
                    .expectBody<List<*>>()
                    .returnResult()
                    .responseBody?.size shouldBe 2

                coVerify { userService.findAll() }
                confirmVerified(userService)
            }
        }

        describe("Creating user") {
            context("With valid data") {
                it("Should create user and return 201") {
                    val command = CreateUserCommand(
                        name = "John",
                        lastName = "Doe",
                        cpf = "12345678901",
                        email = "john.doe@example.com",
                        password = "password123",
                        phone = "11999999999",
                        birth = java.time.LocalDate.of(1990, 1, 1),
                        role = "USER",
                    )
                    val createdUser = userPublicQuery()

                    coEvery { userService.create(any()) } returns Unit
                    coEvery { userService.findByEmail(command.email) } returns createdUser

                    webClient
                        .post()
                        .uri("/user/register")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .bodyValue(command)
                        .exchange()
                        .expectStatus()
                        .isCreated
                        .expectHeader()
                        .exists("Location")

                    coVerify { userService.create(any()) }
                    coVerify { userService.findByEmail(command.email) }
                    confirmVerified(userService)
                }
            }
        }

        describe("Updating user") {
            context("User exists") {
                it("Should update user and return 204") {
                    val userId = UserId()
                    coEvery { userService.update(any()) } returns Unit

                    webClient
                        .put()
                        .uri("/user/$userId")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .bodyValue(
                            mapOf(
                                "name" to "Updated Name",
                                "lastName" to "Updated LastName",
                                "cpf" to "98765432100",
                                "email" to "updated@example.com",
                                "phone" to "11888888888",
                                "birth" to "1995-05-15",
                            ),
                        )
                        .exchange()
                        .expectStatus()
                        .isNoContent

                    coVerify { userService.update(any()) }
                    confirmVerified(userService)
                }
            }
        }

        describe("Changing password") {
            it("Should change password and return 204") {
                val userId = UserId()
                coEvery { userService.changePassword(any()) } returns Unit

                webClient
                    .put()
                    .uri("/user/$userId/password")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(
                        mapOf(
                            "currentPassword" to "oldPassword",
                            "newPassword" to "newPassword",
                        ),
                    )
                    .exchange()
                    .expectStatus()
                    .isNoContent

                coVerify { userService.changePassword(any()) }
                confirmVerified(userService)
            }
        }

        describe("Deleting user") {
            it("Should delete user and return 204") {
                val userId = UserId()
                coEvery { userService.delete(userId) } returns Unit

                webClient
                    .delete()
                    .uri("/user/$userId")
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isNoContent

                coVerify { userService.delete(userId) }
                confirmVerified(userService)
            }
        }

        describe("Finding user by id from login") {
            context("User exists") {
                it("Should find user and return 200") {
                    val userQuery = userPublicQuery()
                    val userId = userQuery.id
                    val userIdString = userId.toString()

                    coEvery { userService.findById(userId) } returns userQuery

                    val authentication = UsernamePasswordAuthenticationToken(userIdString, null, emptyList())
                    
                    webClient
                        .mutateWith(SecurityMockServerConfigurers.mockAuthentication(authentication))
                        .get()
                        .uri("/user/findByLogin")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<UserPublicQuery>()
                        .returnResult()
                        .responseBody?.id shouldBe userId

                    coVerify { userService.findById(userId) }
                    confirmVerified(userService)
                }
            }

            context("User does not exist") {
                it("Should return 404 when user does not exist") {
                    val userId = UserId()
                    val userIdString = userId.toString()

                    coEvery { userService.findById(userId) } throws UserNotFoundException(userId)

                    val authentication = UsernamePasswordAuthenticationToken(userIdString, null, emptyList())
                    
                    webClient
                        .mutateWith(SecurityMockServerConfigurers.mockAuthentication(authentication))
                        .get()
                        .uri("/user/findByLogin")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isNotFound

                    coVerify { userService.findById(userId) }
                    confirmVerified(userService)
                }
            }
        }
    }
}

