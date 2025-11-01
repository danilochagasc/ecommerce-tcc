package com.br.danilo.tcc.account.core.application.user

import com.br.danilo.tcc.account.core.application.address.AddressService
import com.br.danilo.tcc.account.core.application.user.UserServiceTestFixture.changePasswordCommand
import com.br.danilo.tcc.account.core.application.user.UserServiceTestFixture.createUserCommand
import com.br.danilo.tcc.account.core.application.user.UserServiceTestFixture.updateUserCommand
import com.br.danilo.tcc.account.core.domain.auth.PasswordHasher
import com.br.danilo.tcc.account.core.domain.user.PasswordsDoNotMatchException
import com.br.danilo.tcc.account.core.domain.user.User
import com.br.danilo.tcc.account.core.domain.user.UserAlreadyExistsException
import com.br.danilo.tcc.account.core.domain.user.UserId
import com.br.danilo.tcc.account.core.domain.user.UserNotFoundException
import com.br.danilo.tcc.account.core.domain.user.UserRepository
import com.br.danilo.tcc.account.core.domain.user.UserTestFixture.user
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

class UserServiceTest : DescribeSpec() {
    private val repository = mockk<UserRepository>()
    private val addressService = mockk<AddressService>()
    private val passwordHasher = mockk<PasswordHasher>()

    private val service =
        UserService(
            repository = repository,
            addressService = addressService,
            passwordHasher = passwordHasher,
        )

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(repository, addressService, passwordHasher)
    }

    init {
        describe("Finding all users") {
            it("Should return all users") {
                val user1 = user()
                val user2 = user()
                coEvery { repository.findAll() } returns listOf(user1, user2)

                val result = runBlocking { service.findAll() }

                result.size shouldBe 2
                coVerify { repository.findAll() }
            }

            it("Should return empty list when no users exist") {
                coEvery { repository.findAll() } returns emptyList()

                val result = runBlocking { service.findAll() }

                result shouldBe emptyList()
                coVerify { repository.findAll() }
            }
        }

        describe("Finding user by id") {
            context("User exists") {
                it("Should return user") {
                    val user = user()
                    coEvery { repository.findById(user.id) } returns user

                    val result = runBlocking { service.findById(user.id) }

                    result.id shouldBe user.id
                    result.name shouldBe user.name
                    coVerify { repository.findById(user.id) }
                }
            }

            context("User does not exist") {
                it("Should throw UserNotFoundException") {
                    val userId = UserId()
                    coEvery { repository.findById(userId) } returns null

                    shouldThrow<UserNotFoundException> {
                        runBlocking { service.findById(userId) }
                    }

                    coVerify { repository.findById(userId) }
                }
            }
        }

        describe("Creating user") {
            context("With valid data") {
                it("Should create user successfully") {
                    val command = createUserCommand()
                    coEvery { repository.findByEmail(command.email) } returns null
                    coEvery { passwordHasher.hash(command.password) } returns "hashedPassword"
                    coEvery { repository.create(any()) } returns Unit

                    runBlocking { service.create(command) }

                    coVerify { repository.findByEmail(command.email) }
                    coVerify { passwordHasher.hash(command.password) }
                    coVerify { repository.create(any()) }
                }
            }

            context("When email already exists") {
                it("Should throw UserAlreadyExistsException") {
                    val command = createUserCommand()
                    val existingUser = user(email = command.email)
                    coEvery { repository.findByEmail(command.email) } returns existingUser

                    shouldThrow<UserAlreadyExistsException> {
                        runBlocking { service.create(command) }
                    }

                    coVerify { repository.findByEmail(command.email) }
                    coVerify(exactly = 0) { repository.create(any()) }
                }
            }
        }

        describe("Updating user") {
            context("User exists") {
                it("Should update user successfully") {
                    val user = user()
                    val command = updateUserCommand(id = user.id)
                    coEvery { repository.findById(user.id) } returns user
                    coEvery { repository.findByEmail(command.email) } returns null
                    coEvery { repository.update(any()) } returns Unit

                    runBlocking { service.update(command) }

                    coVerify { repository.findById(user.id) }
                    coVerify { repository.findByEmail(command.email) }
                    coVerify { repository.update(any()) }
                }
            }

            context("User does not exist") {
                it("Should throw UserNotFoundException") {
                    val command = updateUserCommand()
                    coEvery { repository.findById(command.id) } returns null

                    shouldThrow<UserNotFoundException> {
                        runBlocking { service.update(command) }
                    }

                    coVerify { repository.findById(command.id) }
                    coVerify(exactly = 0) { repository.update(any()) }
                }
            }

            context("Email already exists for another user") {
                it("Should throw UserAlreadyExistsException") {
                    val user = user()
                    val command = updateUserCommand(id = user.id)
                    val anotherUser = user(email = command.email)
                    coEvery { repository.findById(user.id) } returns user
                    coEvery { repository.findByEmail(command.email) } returns anotherUser

                    shouldThrow<UserAlreadyExistsException> {
                        runBlocking { service.update(command) }
                    }

                    coVerify { repository.findById(user.id) }
                    coVerify { repository.findByEmail(command.email) }
                    coVerify(exactly = 0) { repository.update(any()) }
                }
            }
        }

        describe("Changing password") {
            context("User exists with correct current password") {
                it("Should change password successfully") {
                    val user = user(password = "oldHashedPassword")
                    val command = changePasswordCommand(id = user.id)
                    coEvery { repository.findById(user.id) } returns user
                    coEvery { passwordHasher.verify(command.currentPassword, user.password) } returns true
                    coEvery { passwordHasher.hash(command.newPassword) } returns "newHashedPassword"
                    coEvery { repository.update(any()) } returns Unit

                    runBlocking { service.changePassword(command) }

                    coVerify { repository.findById(user.id) }
                    coVerify { passwordHasher.verify(command.currentPassword, user.password) }
                    coVerify { passwordHasher.hash(command.newPassword) }
                    coVerify { repository.update(any()) }
                }
            }

            context("User does not exist") {
                it("Should throw UserNotFoundException") {
                    val command = changePasswordCommand()
                    coEvery { repository.findById(command.id) } returns null

                    shouldThrow<UserNotFoundException> {
                        runBlocking { service.changePassword(command) }
                    }

                    coVerify { repository.findById(command.id) }
                    coVerify(exactly = 0) { repository.update(any()) }
                }
            }

            context("Current password is incorrect") {
                it("Should throw PasswordsDoNotMatchException") {
                    val user = user(password = "oldHashedPassword")
                    val command = changePasswordCommand(id = user.id)
                    coEvery { repository.findById(user.id) } returns user
                    coEvery { passwordHasher.verify(command.currentPassword, user.password) } returns false

                    shouldThrow<PasswordsDoNotMatchException> {
                        runBlocking { service.changePassword(command) }
                    }

                    coVerify { repository.findById(user.id) }
                    coVerify { passwordHasher.verify(command.currentPassword, user.password) }
                    coVerify(exactly = 0) { repository.update(any()) }
                }
            }
        }

        describe("Deleting user") {
            context("User exists") {
                it("Should delete user successfully") {
                    val user = user()
                    coEvery { repository.findById(user.id) } returns user
                    coEvery { addressService.deleteAllByUserId(user.id) } returns Unit
                    coEvery { repository.delete(user.id) } returns Unit

                    runBlocking { service.delete(user.id) }

                    coVerify { repository.findById(user.id) }
                    coVerify { addressService.deleteAllByUserId(user.id) }
                    coVerify { repository.delete(user.id) }
                }
            }

            context("User does not exist") {
                it("Should throw UserNotFoundException") {
                    val userId = UserId()
                    coEvery { repository.findById(userId) } returns null

                    shouldThrow<UserNotFoundException> {
                        runBlocking { service.delete(userId) }
                    }

                    coVerify { repository.findById(userId) }
                    coVerify(exactly = 0) { addressService.deleteAllByUserId(any()) }
                    coVerify(exactly = 0) { repository.delete(any()) }
                }
            }
        }
    }
}

