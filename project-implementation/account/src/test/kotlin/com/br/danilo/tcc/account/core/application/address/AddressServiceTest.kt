package com.br.danilo.tcc.account.core.application.address

import com.br.danilo.tcc.account.core.application.address.AddressServiceTestFixture.createAddressCommand
import com.br.danilo.tcc.account.core.application.address.AddressServiceTestFixture.updateAddressCommand
import com.br.danilo.tcc.account.core.domain.address.Address
import com.br.danilo.tcc.account.core.domain.address.AddressId
import com.br.danilo.tcc.account.core.domain.address.AddressNotFoundException
import com.br.danilo.tcc.account.core.domain.address.AddressRepository
import com.br.danilo.tcc.account.core.domain.address.AddressTestFixture.address
import com.br.danilo.tcc.account.core.domain.user.UserId
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

class AddressServiceTest : DescribeSpec() {
    private val repository = mockk<AddressRepository>()

    private val service = AddressService(
        repository = repository,
    )

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(repository)
    }

    init {
        describe("Finding address by id") {
            context("Address exists") {
                it("Should return address") {
                    val address = address()
                    coEvery { repository.findById(address.id) } returns address

                    val result = runBlocking { service.findById(address.id) }

                    result shouldNotBe null
                    result!!.id shouldBe address.id
                    result.street shouldBe address.street
                    coVerify { repository.findById(address.id) }
                }
            }

            context("Address does not exist") {
                it("Should return null") {
                    val addressId = AddressId()
                    coEvery { repository.findById(addressId) } returns null

                    val result = runBlocking { service.findById(addressId) }

                    result shouldBe null
                    coVerify { repository.findById(addressId) }
                }
            }
        }

        describe("Finding all addresses by user id") {
            it("Should return all addresses for user") {
                val userId = UserId()
                val address1 = address(userId = userId)
                val address2 = address(userId = userId)
                coEvery { repository.findAllByUserId(userId) } returns listOf(address1, address2)

                val result = runBlocking { service.findAllByUserId(userId) }

                result.size shouldBe 2
                coVerify { repository.findAllByUserId(userId) }
            }

            it("Should return empty list when no addresses exist") {
                val userId = UserId()
                coEvery { repository.findAllByUserId(userId) } returns emptyList()

                val result = runBlocking { service.findAllByUserId(userId) }

                result shouldBe emptyList()
                coVerify { repository.findAllByUserId(userId) }
            }
        }

        describe("Creating address") {
            context("With valid data") {
                it("Should create address successfully") {
                    val command = createAddressCommand()
                    coEvery { repository.create(any()) } returns Unit

                    val result = runBlocking { service.create(command) }

                    result shouldNotBe null
                    coVerify { repository.create(any()) }
                }
            }
        }

        describe("Updating address") {
            context("Address exists") {
                it("Should update address successfully") {
                    val address = address()
                    val command = updateAddressCommand(id = address.id)
                    coEvery { repository.findById(address.id) } returns address
                    coEvery { repository.update(any()) } returns Unit

                    runBlocking { service.update(command) }

                    coVerify { repository.findById(address.id) }
                    coVerify { repository.update(any()) }
                }
            }

            context("Address does not exist") {
                it("Should throw AddressNotFoundException") {
                    val command = updateAddressCommand()
                    coEvery { repository.findById(command.id) } returns null

                    shouldThrow<AddressNotFoundException> {
                        runBlocking { service.update(command) }
                    }

                    coVerify { repository.findById(command.id) }
                    coVerify(exactly = 0) { repository.update(any()) }
                }
            }
        }

        describe("Deleting address") {
            context("Address exists") {
                it("Should delete address successfully") {
                    val address = address()
                    coEvery { repository.findById(address.id) } returns address
                    coEvery { repository.delete(address.id) } returns Unit

                    runBlocking { service.delete(address.id) }

                    coVerify { repository.findById(address.id) }
                    coVerify { repository.delete(address.id) }
                }
            }

            context("Address does not exist") {
                it("Should throw AddressNotFoundException") {
                    val addressId = AddressId()
                    coEvery { repository.findById(addressId) } returns null

                    shouldThrow<AddressNotFoundException> {
                        runBlocking { service.delete(addressId) }
                    }

                    coVerify { repository.findById(addressId) }
                    coVerify(exactly = 0) { repository.delete(any()) }
                }
            }
        }

        describe("Deleting all addresses by user id") {
            it("Should delete all addresses for user") {
                val userId = UserId()
                coEvery { repository.deleteAllByUserId(userId) } returns Unit

                runBlocking { service.deleteAllByUserId(userId) }

                coVerify { repository.deleteAllByUserId(userId) }
            }
        }
    }
}

