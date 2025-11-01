package com.br.danilo.tcc.account.core.domain.user

import com.br.danilo.tcc.account.core.domain.user.UserTestFixture.user
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.valiktor.constraints.Email
import org.valiktor.constraints.GreaterOrEqual
import org.valiktor.constraints.Size
import org.valiktor.constraints.Matches
import org.valiktor.constraints.NotBlank
import org.valiktor.test.shouldFailValidation
import java.time.LocalDate

class UserTest : DescribeSpec() {

    init {
        describe("Creating a User") {
            context("A valid one") {
                val user = user()

                it("Should be created properly") {
                    user.id shouldNotBe null
                    user.name shouldBe "John"
                    user.lastName shouldBe "Doe"
                    user.cpf shouldBe "12345678901"
                    user.email shouldBe "john.doe@example.com"
                    user.password shouldBe "hashedPassword123"
                    user.phone shouldBe "11999999999"
                    user.birth shouldBe LocalDate.of(1990, 1, 1)
                    user.role shouldBe Role.USER
                    user.addresses shouldBe emptySet()
                }
            }

            context("Invalid name") {
                it("Should fail when name is blank") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "",
                            lastName = "Doe",
                            cpf = "12345678901",
                            email = "john.doe@example.com",
                            password = "password123",
                            phone = "11999999999",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::name, "", NotBlank)
                    }
                }
            }

            context("Invalid lastName") {
                it("Should fail when lastName is blank") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "",
                            cpf = "12345678901",
                            email = "john.doe@example.com",
                            password = "password123",
                            phone = "11999999999",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::lastName, "", NotBlank)
                    }
                }
            }

            context("Invalid CPF") {
                it("Should fail when CPF is blank") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "Doe",
                            cpf = "",
                            email = "john.doe@example.com",
                            password = "password123",
                            phone = "11999999999",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::cpf, "", NotBlank)
                        expect(User::cpf, "", Matches(Regex("^[0-9]{11}$")))
                    }
                }

                it("Should fail when CPF does not match pattern") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "Doe",
                            cpf = "123",
                            email = "john.doe@example.com",
                            password = "password123",
                            phone = "11999999999",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::cpf, "123", Matches(Regex("^[0-9]{11}$")))
                    }
                }
            }

            context("Invalid email") {
                it("Should fail when email is blank") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "Doe",
                            cpf = "12345678901",
                            email = "",
                            password = "password123",
                            phone = "11999999999",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::email, "", NotBlank)
                        expect(User::email, "", Email)
                    }
                }

                it("Should fail when email is invalid") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "Doe",
                            cpf = "12345678901",
                            email = "invalid-email",
                            password = "password123",
                            phone = "11999999999",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::email, "invalid-email", Email)
                    }
                }
            }

            context("Invalid password") {
                it("Should fail when password is blank") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "Doe",
                            cpf = "12345678901",
                            email = "john.doe@example.com",
                            password = "",
                            phone = "11999999999",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::password, "", NotBlank)
                    }
                }
            }

            context("Invalid phone") {
                it("Should fail when phone is blank") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "Doe",
                            cpf = "12345678901",
                            email = "john.doe@example.com",
                            password = "password123",
                            phone = "",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::phone, "", NotBlank)
                        expect(User::phone, "", Size(min = 8, max = 15))
                    }
                }

                it("Should fail when phone is too short") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "Doe",
                            cpf = "12345678901",
                            email = "john.doe@example.com",
                            password = "password123",
                            phone = "1234567",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::phone, "1234567", Size(min = 8, max = 15))
                    }
                }

                it("Should fail when phone is too long") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "Doe",
                            cpf = "12345678901",
                            email = "john.doe@example.com",
                            password = "password123",
                            phone = "1234567890123456",
                            birth = LocalDate.of(1990, 1, 1),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::phone, "1234567890123456", Size(min = 8, max = 15))
                    }
                }
            }

            context("Invalid birth date") {
                it("Should fail when birth date is before 1900") {
                    shouldFailValidation<User> {
                        User.create(
                            name = "John",
                            lastName = "Doe",
                            cpf = "12345678901",
                            email = "john.doe@example.com",
                            password = "password123",
                            phone = "11999999999",
                            birth = LocalDate.of(1899, 12, 31),
                            role = Role.USER,
                        )
                    }.verify {
                        expect(User::birth, LocalDate.of(1899, 12, 31), GreaterOrEqual(LocalDate.of(1900, 1, 1)))
                    }
                }
            }
        }

        describe("Updating a User") {
            context("With valid data") {
                it("Should update user successfully") {
                    val user = user()
                    val updated = user.update(
                        name = "Jane",
                        lastName = "Smith",
                        cpf = "98765432100",
                        email = "jane.smith@example.com",
                        password = user.password,
                        phone = "11888888888",
                        birth = LocalDate.of(1995, 5, 15),
                    )

                    updated.name shouldBe "Jane"
                    updated.lastName shouldBe "Smith"
                    updated.cpf shouldBe "98765432100"
                    updated.email shouldBe "jane.smith@example.com"
                    updated.phone shouldBe "11888888888"
                    updated.birth shouldBe LocalDate.of(1995, 5, 15)
                    updated.id shouldBe user.id
                    updated.role shouldBe user.role
                }
            }
        }

        describe("Changing password") {
            context("With valid password") {
                it("Should change password successfully") {
                    val user = user()
                    val newPassword = "newHashedPassword123"

                    val updated = user.changePassword(newPassword)

                    updated.password shouldBe newPassword
                    updated.id shouldBe user.id
                    updated.name shouldBe user.name
                }
            }
        }
    }
}

