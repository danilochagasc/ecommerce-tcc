package com.danilo.tcc.stock.core.domain.product

import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.product.ProductTestFixture.product
import com.danilo.tcc.stock.core.domain.product.ProductTestFixture.productWithLowQuantity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.valiktor.constraints.Greater
import org.valiktor.constraints.NotBlank
import org.valiktor.test.shouldFailValidation

class ProductTest : DescribeSpec() {
    init {
        describe("Creating a Product") {
            context("A valid one") {
                val product = product()

                it("Should be created properly") {
                    product.id shouldNotBe null
                    product.name shouldBe "Product 1"
                    product.description shouldBe "Product description"
                    product.imageUrl shouldBe "https://example.com/image.jpg"
                    product.price shouldBe 10.0
                    product.quantity shouldBe 100
                    product.categoryId shouldNotBe null
                }
            }

            context("Invalid name") {
                it("Should fail when name is blank") {
                    shouldFailValidation<Product> {
                        Product.create(
                            name = "",
                            description = "Product description",
                            imageUrl = "https://example.com/image.jpg",
                            price = 10.0,
                            quantity = 100,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::name, "", NotBlank)
                    }
                }
            }

            context("Invalid description") {
                it("Should fail when description is blank") {
                    shouldFailValidation<Product> {
                        Product.create(
                            name = "Product 1",
                            description = "",
                            imageUrl = "https://example.com/image.jpg",
                            price = 10.0,
                            quantity = 100,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::description, "", NotBlank)
                    }
                }
            }

            context("Invalid price") {
                it("Should fail when price is zero") {
                    shouldFailValidation<Product> {
                        Product.create(
                            name = "Product 1",
                            description = "Product description",
                            imageUrl = "https://example.com/image.jpg",
                            price = 0.0,
                            quantity = 100,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::price, 0.0, Greater(0.0))
                    }
                }

                it("Should fail when price is negative") {
                    shouldFailValidation<Product> {
                        Product.create(
                            name = "Product 1",
                            description = "Product description",
                            imageUrl = "https://example.com/image.jpg",
                            price = -10.0,
                            quantity = 100,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::price, -10.0, Greater(0.0))
                    }
                }
            }

            context("Invalid quantity") {
                it("Should fail when quantity is zero") {
                    shouldFailValidation<Product> {
                        Product.create(
                            name = "Product 1",
                            description = "Product description",
                            imageUrl = "https://example.com/image.jpg",
                            price = 10.0,
                            quantity = 0,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::quantity, 0, Greater(0))
                    }
                }

                it("Should fail when quantity is negative") {
                    shouldFailValidation<Product> {
                        Product.create(
                            name = "Product 1",
                            description = "Product description",
                            imageUrl = "https://example.com/image.jpg",
                            price = 10.0,
                            quantity = -5,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::quantity, -5, Greater(0))
                    }
                }
            }
        }

        describe("Updating a Product") {
            context("With valid data") {
                it("Should update product successfully") {
                    val product = product()
                    val categoryId = CategoryId()

                    val updated =
                        product.update(
                            name = "Updated Product",
                            description = "Updated description",
                            price = 15.0,
                            quantity = 150,
                            categoryId = categoryId,
                        )

                    updated.name shouldBe "Updated Product"
                    updated.description shouldBe "Updated description"
                    updated.price shouldBe 15.0
                    updated.quantity shouldBe 150
                    updated.categoryId shouldBe categoryId
                    updated.id shouldBe product.id
                }
            }

            context("With invalid data") {
                it("Should fail when name is blank") {
                    val product = product()

                    shouldFailValidation<Product> {
                        product.update(
                            name = "",
                            description = "Description",
                            price = 10.0,
                            quantity = 10,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::name, "", NotBlank)
                    }
                }

                it("Should fail when description is blank") {
                    val product = product()

                    shouldFailValidation<Product> {
                        product.update(
                            name = "Product",
                            description = "",
                            price = 10.0,
                            quantity = 10,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::description, "", NotBlank)
                    }
                }

                it("Should fail when price is zero") {
                    val product = product()

                    shouldFailValidation<Product> {
                        product.update(
                            name = "Product",
                            description = "Description",
                            price = 0.0,
                            quantity = 10,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::price, 0.0, Greater(0.0))
                    }
                }

                it("Should fail when price is negative") {
                    val product = product()

                    shouldFailValidation<Product> {
                        product.update(
                            name = "Product",
                            description = "Description",
                            price = -10.0,
                            quantity = 10,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::price, -10.0, Greater(0.0))
                    }
                }

                it("Should fail when quantity is zero") {
                    val product = product()

                    shouldFailValidation<Product> {
                        product.update(
                            name = "Product",
                            description = "Description",
                            price = 10.0,
                            quantity = 0,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::quantity, 0, Greater(0))
                    }
                }

                it("Should fail when quantity is negative") {
                    val product = product()

                    shouldFailValidation<Product> {
                        product.update(
                            name = "Product",
                            description = "Description",
                            price = 10.0,
                            quantity = -5,
                            categoryId = CategoryId(),
                        )
                    }.verify {
                        expect(Product::quantity, -5, Greater(0))
                    }
                }
            }
        }

        describe("Changing product image") {
            context("Valid image URL") {
                it("Should update image URL") {
                    val product = product()
                    val newImageUrl = "https://example.com/new-image.jpg"

                    val updated = product.changeImage(newImageUrl)

                    updated.imageUrl shouldBe newImageUrl
                    updated.id shouldBe product.id
                    updated.name shouldBe product.name
                    updated.price shouldBe product.price
                }
            }
        }

        describe("Decreasing product quantity") {
            context("With sufficient quantity") {
                it("Should decrease quantity successfully") {
                    val product = product(quantity = 100)

                    val updated = product.decreaseQuantity(30)

                    updated.quantity shouldBe 70
                    updated.id shouldBe product.id
                }
            }

            context("With insufficient quantity") {
                it("Should throw InsufficientProductQuantityException") {
                    val product = productWithLowQuantity()

                    val exception =
                        shouldThrow<InsufficientProductQuantityException> {
                            product.decreaseQuantity(10)
                        }

                    exception.productId shouldBe product.id
                    exception.availableQuantity shouldBe 5
                    exception.requestedQuantity shouldBe 10
                }
            }
        }

        describe("Increasing product quantity") {
            context("With valid amount") {
                it("Should increase quantity successfully") {
                    val product = product(quantity = 100)

                    val updated = product.increaseQuantity(50)

                    updated.quantity shouldBe 150
                    updated.id shouldBe product.id
                    updated.name shouldBe product.name
                }
            }
        }
    }
}
