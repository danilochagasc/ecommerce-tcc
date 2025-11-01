package com.br.danilo.tcc.account.adapter.http.handler

import com.br.danilo.tcc.account.adapter.router.createWebTestClient
import com.br.danilo.tcc.account.core.application.auth.AuthService
import com.br.danilo.tcc.account.core.application.auth.command.LoginCommand
import com.br.danilo.tcc.account.core.application.auth.dto.LoginResponse
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
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest
@Import(TestSecurityConfig::class)
class LoginHandlerTest(
    applicationContext: ApplicationContext,
) : DescribeSpec() {
    @MockkBean
    private lateinit var authService: AuthService

    private val webClient = createWebTestClient(applicationContext)

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(authService)
    }

    init {
        describe("Login") {
            context("With valid credentials") {
                it("Should login and return 200 with token") {
                    val command = LoginCommand(
                        email = "john.doe@example.com",
                        password = "password123",
                    )
                    val loginResponse = LoginResponse(
                        token = "jwt.token.here",
                        expiresAt = 1234567890L,
                    )

                    coEvery { authService.login(any()) } returns loginResponse

                    webClient
                        .post()
                        .uri("/auth/login")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .bodyValue(command)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<LoginResponse>()
                        .returnResult()
                        .responseBody?.token shouldBe loginResponse.token

                    coVerify { authService.login(any()) }
                    confirmVerified(authService)
                }
            }
        }
    }
}

