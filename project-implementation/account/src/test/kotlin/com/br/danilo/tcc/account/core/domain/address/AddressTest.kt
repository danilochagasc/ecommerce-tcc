package com.br.danilo.tcc.account.core.domain.address

import com.br.danilo.tcc.account.core.domain.address.AddressTestFixture.address
import com.br.danilo.tcc.account.core.domain.user.UserId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.valiktor.constraints.Size
import org.valiktor.constraints.NotBlank
import org.valiktor.test.shouldFailValidation

class AddressTest : DescribeSpec() {

    init {
        describe("Creating an Address") {
            context("A valid one") {
                val userId = UserId()
                val address = address(userId = userId)

                it("Should be created properly") {
                    address.id shouldNotBe null
                    address.userId shouldBe userId
                    address.street shouldBe "Main Street"
                    address.number shouldBe "123"
                    address.complement shouldBe null
                    address.neighborhood shouldBe "Downtown"
                    address.city shouldBe "São Paulo"
                    address.state shouldBe "SP"
                    address.zipCode shouldBe "01234567"
                }
            }

            context("Invalid street") {
                it("Should fail when street is blank") {
                    shouldFailValidation<Address> {
                        Address.create(
                            userId = UserId(),
                            street = "",
                            number = "123",
                            complement = null,
                            neighborhood = "Downtown",
                            city = "São Paulo",
                            state = "SP",
                            zipCode = "01234567",
                        )
                    }.verify {
                        expect(Address::street, "", NotBlank)
                    }
                }
            }

            context("Invalid number") {
                it("Should fail when number is blank") {
                    shouldFailValidation<Address> {
                        Address.create(
                            userId = UserId(),
                            street = "Main Street",
                            number = "",
                            complement = null,
                            neighborhood = "Downtown",
                            city = "São Paulo",
                            state = "SP",
                            zipCode = "01234567",
                        )
                    }.verify {
                        expect(Address::number, "", NotBlank)
                    }
                }
            }

            context("Invalid neighborhood") {
                it("Should fail when neighborhood is blank") {
                    shouldFailValidation<Address> {
                        Address.create(
                            userId = UserId(),
                            street = "Main Street",
                            number = "123",
                            complement = null,
                            neighborhood = "",
                            city = "São Paulo",
                            state = "SP",
                            zipCode = "01234567",
                        )
                    }.verify {
                        expect(Address::neighborhood, "", NotBlank)
                    }
                }
            }

            context("Invalid city") {
                it("Should fail when city is blank") {
                    shouldFailValidation<Address> {
                        Address.create(
                            userId = UserId(),
                            street = "Main Street",
                            number = "123",
                            complement = null,
                            neighborhood = "Downtown",
                            city = "",
                            state = "SP",
                            zipCode = "01234567",
                        )
                    }.verify {
                        expect(Address::city, "", NotBlank)
                    }
                }
            }

            context("Invalid state") {
                it("Should fail when state is blank") {
                    shouldFailValidation<Address> {
                        Address.create(
                            userId = UserId(),
                            street = "Main Street",
                            number = "123",
                            complement = null,
                            neighborhood = "Downtown",
                            city = "São Paulo",
                            state = "",
                            zipCode = "01234567",
                        )
                    }.verify {
                        expect(Address::state, "", NotBlank)
                        expect(Address::state, "", Size(min = 2, max = 2))
                    }
                }

                it("Should fail when state does not have exactly 2 characters") {
                    shouldFailValidation<Address> {
                        Address.create(
                            userId = UserId(),
                            street = "Main Street",
                            number = "123",
                            complement = null,
                            neighborhood = "Downtown",
                            city = "São Paulo",
                            state = "S",
                            zipCode = "01234567",
                        )
                    }.verify {
                        expect(Address::state, "S", Size(min = 2, max = 2))
                    }
                }
            }

            context("Invalid zipCode") {
                it("Should fail when zipCode is blank") {
                    shouldFailValidation<Address> {
                        Address.create(
                            userId = UserId(),
                            street = "Main Street",
                            number = "123",
                            complement = null,
                            neighborhood = "Downtown",
                            city = "São Paulo",
                            state = "SP",
                            zipCode = "",
                        )
                    }.verify {
                        expect(Address::zipCode, "", NotBlank)
                        expect(Address::zipCode, "", Size(min = 8, max = 8))
                    }
                }

                it("Should fail when zipCode does not have exactly 8 characters") {
                    shouldFailValidation<Address> {
                        Address.create(
                            userId = UserId(),
                            street = "Main Street",
                            number = "123",
                            complement = null,
                            neighborhood = "Downtown",
                            city = "São Paulo",
                            state = "SP",
                            zipCode = "1234567",
                        )
                    }.verify {
                        expect(Address::zipCode, "1234567", Size(min = 8, max = 8))
                    }
                }
            }
        }

        describe("Updating an Address") {
            context("With valid data") {
                it("Should update address successfully") {
                    val address = address()

                    val updated = address.update(
                        street = "Updated Street",
                        number = "456",
                        complement = "Apt 101",
                        neighborhood = "Uptown",
                        city = "Rio de Janeiro",
                        state = "RJ",
                        zipCode = "20000000",
                    )

                    updated.street shouldBe "Updated Street"
                    updated.number shouldBe "456"
                    updated.complement shouldBe "Apt 101"
                    updated.neighborhood shouldBe "Uptown"
                    updated.city shouldBe "Rio de Janeiro"
                    updated.state shouldBe "RJ"
                    updated.zipCode shouldBe "20000000"
                    updated.id shouldBe address.id
                    updated.userId shouldBe address.userId
                }
            }
        }
    }
}

