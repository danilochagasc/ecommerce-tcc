package com.br.danilo.tcc.account.adapter.http.handler

import com.br.danilo.tcc.account.adapter.http.handler.AddressHandlerTestFixture.addressQuery
import com.br.danilo.tcc.account.adapter.http.handler.AddressHandlerTestFixture.anotherAddressQuery
import com.br.danilo.tcc.account.adapter.router.createWebTestClient
import com.br.danilo.tcc.account.core.application.address.AddressService
import com.br.danilo.tcc.account.core.application.address.command.CreateAddressCommand
import com.br.danilo.tcc.account.core.domain.address.AddressId
import com.br.danilo.tcc.account.core.domain.address.AddressNotFoundException
import com.br.danilo.tcc.account.core.domain.user.UserId
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
class AddressHandlerTest(
    applicationContext: ApplicationContext,
) : DescribeSpec() {
    @MockkBean
    private lateinit var addressService: AddressService

    private val webClient = createWebTestClient(applicationContext)

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(addressService)
    }

    init {
        describe("Finding all addresses by user id") {
            context("User has addresses") {
                it("Should return all addresses and return 200") {
                    val userId = UserId()
                    val addresses = listOf(addressQuery(), anotherAddressQuery())

                    coEvery { addressService.findAllByUserId(userId) } returns addresses

                    webClient
                        .get()
                        .uri("/address/$userId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectHeader()
                        .contentType(APPLICATION_JSON)
                        .expectBody<List<*>>()
                        .returnResult()
                        .responseBody?.size shouldBe 2

                    coVerify { addressService.findAllByUserId(userId) }
                    confirmVerified(addressService)
                }
            }

            context("User has no addresses") {
                it("Should return empty list and return 200") {
                    val userId = UserId()

                    coEvery { addressService.findAllByUserId(userId) } returns emptyList()

                    webClient
                        .get()
                        .uri("/address/$userId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<List<*>>()
                        .returnResult()
                        .responseBody?.size shouldBe 0

                    coVerify { addressService.findAllByUserId(userId) }
                    confirmVerified(addressService)
                }
            }
        }

        describe("Creating address") {
            it("Should create address and return 201") {
                val addressId = AddressId()
                val command = CreateAddressCommand(
                    userId = UserId(),
                    street = "Main Street",
                    number = "123",
                    complement = null,
                    neighborhood = "Downtown",
                    city = "São Paulo",
                    state = "SP",
                    zipCode = "01234567",
                )

                coEvery { addressService.create(any()) } returns addressId

                webClient
                    .post()
                    .uri("/address")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(command)
                    .exchange()
                    .expectStatus()
                    .isCreated
                    .expectHeader()
                    .valueEquals("Location", "/address/$addressId")

                coVerify { addressService.create(any()) }
                confirmVerified(addressService)
            }
        }

        describe("Updating address") {
            context("Address exists") {
                it("Should update address and return 204") {
                    val addressId = AddressId()

                    coEvery { addressService.update(any()) } returns Unit

                    webClient
                        .put()
                        .uri("/address/$addressId")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .bodyValue(
                            mapOf(
                                "street" to "Updated Street",
                                "number" to "456",
                                "complement" to "Apt 101",
                                "neighborhood" to "Uptown",
                                "city" to "Rio de Janeiro",
                                "state" to "RJ",
                                "zipCode" to "20000000",
                            ),
                        )
                        .exchange()
                        .expectStatus()
                        .isNoContent

                    coVerify { addressService.update(any()) }
                    confirmVerified(addressService)
                }
            }

            context("Address does not exist") {
                it("Should return 404 when address does not exist") {
                    val addressId = AddressId()

                    coEvery { addressService.update(any()) } throws AddressNotFoundException(addressId)

                    webClient
                        .put()
                        .uri("/address/$addressId")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .bodyValue(
                            mapOf(
                                "street" to "Updated Street",
                                "number" to "456",
                                "neighborhood" to "Uptown",
                                "city" to "Rio de Janeiro",
                                "state" to "RJ",
                                "zipCode" to "20000000",
                            ),
                        )
                        .exchange()
                        .expectStatus()
                        .isNotFound

                    coVerify { addressService.update(any()) }
                    confirmVerified(addressService)
                }
            }
        }

        describe("Deleting address") {
            context("Address exists") {
                it("Should delete address and return 204") {
                    val addressId = AddressId()

                    coEvery { addressService.delete(addressId) } returns Unit

                    webClient
                        .delete()
                        .uri("/address/$addressId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isNoContent

                    coVerify { addressService.delete(addressId) }
                    confirmVerified(addressService)
                }
            }

            context("Address does not exist") {
                it("Should return 404 when address does not exist") {
                    val addressId = AddressId()

                    coEvery { addressService.delete(addressId) } throws AddressNotFoundException(addressId)

                    webClient
                        .delete()
                        .uri("/address/$addressId")
                        .accept(APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isNotFound

                    coVerify { addressService.delete(addressId) }
                    confirmVerified(addressService)
                }
            }
        }
    }
}

