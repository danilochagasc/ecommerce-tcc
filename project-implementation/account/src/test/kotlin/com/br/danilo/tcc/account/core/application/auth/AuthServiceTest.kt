package com.br.danilo.tcc.account.core.application.auth

import com.br.danilo.tcc.account.core.application.auth.AuthServiceTestFixture.loginCommand
import com.br.danilo.tcc.account.core.domain.auth.PasswordHasher
import com.br.danilo.tcc.account.core.domain.auth.TokenClaims
import com.br.danilo.tcc.account.core.domain.auth.TokenProvider
import com.br.danilo.tcc.account.core.domain.user.InvalidCredentialsException
import com.br.danilo.tcc.account.core.domain.user.UserNotFoundExceptionByEmail
import com.br.danilo.tcc.account.core.domain.user.UserRepository
import com.br.danilo.tcc.account.core.domain.user.UserTestFixture.user
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

class AuthServiceTest : DescribeSpec() {
    private val userRepository = mockk<UserRepository>()
    private val passwordHasher = mockk<PasswordHasher>()
    private val tokenProvider = mockk<TokenProvider>()

    private val service = AuthService(
        userRepository = userRepository,
        passwordHasher = passwordHasher,
        tokenProvider = tokenProvider,
    )

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(userRepository, passwordHasher, tokenProvider)
    }

    init {
        describe("Login") {
            context("With valid credentials") {
                it("Should return login response with token") {
                    val command = loginCommand()
                    val user = user(email = command.email, password = "hashedPassword")
                    val token = "jwt.token.here"
                    val expiresAt = 1234567890L

                    val tokenClaims = TokenClaims(
                        userId = user.id,
                        email = user.email,
                        role = user.role,
                        expiresAtEpochSeconds = expiresAt,
                    )
                    coEvery { userRepository.findByEmail(command.email) } returns user
                    coEvery { passwordHasher.verify(command.password, user.password) } returns true
                    coEvery { tokenProvider.generateToken(user.id, user.email, user.role) } returns token
                    coEvery { tokenProvider.getClaims(token) } returns tokenClaims

                    val result = runBlocking { service.login(command) }

                    result shouldNotBe null
                    result.token shouldBe token
                    result.expiresAt shouldBe expiresAt
                    coVerify { userRepository.findByEmail(command.email) }
                    coVerify { passwordHasher.verify(command.password, user.password) }
                    coVerify { tokenProvider.generateToken(user.id, user.email, user.role) }
                    coVerify { tokenProvider.getClaims(token) }
                }
            }

            context("User does not exist") {
                it("Should throw UserNotFoundExceptionByEmail") {
                    val command = loginCommand()
                    coEvery { userRepository.findByEmail(command.email) } returns null

                    shouldThrow<UserNotFoundExceptionByEmail> {
                        runBlocking { service.login(command) }
                    }

                    coVerify { userRepository.findByEmail(command.email) }
                    coVerify(exactly = 0) { passwordHasher.verify(any(), any()) }
                }
            }

            context("Invalid password") {
                it("Should throw InvalidCredentialsException") {
                    val command = loginCommand()
                    val user = user(email = command.email, password = "hashedPassword")
                    coEvery { userRepository.findByEmail(command.email) } returns user
                    coEvery { passwordHasher.verify(command.password, user.password) } returns false

                    shouldThrow<InvalidCredentialsException> {
                        runBlocking { service.login(command) }
                    }

                    coVerify { userRepository.findByEmail(command.email) }
                    coVerify { passwordHasher.verify(command.password, user.password) }
                    coVerify(exactly = 0) { tokenProvider.generateToken(any(), any(), any()) }
                }
            }
        }
    }
}

