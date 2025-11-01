package com.danilo.tcc.stock.core.domain.category

import com.danilo.tcc.stock.core.domain.category.CategoryTestFixture.category
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.valiktor.constraints.NotBlank
import org.valiktor.test.shouldFailValidation

class CategoryTest : DescribeSpec() {
    init {
        describe("Creating a Category") {
            context("A valid one") {
                val category = category()

                it("Should be created properly") {
                    category.id shouldNotBe null
                    category.name shouldBe "Category 1"
                }
            }

            context("Invalid category name") {
                it("Should fail when name is blank") {
                    shouldFailValidation<Category> {
                        Category.create(name = "")
                    }.verify {
                        expect(Category::name, "", NotBlank)
                    }
                }

                it("Should fail when name is whitespace only") {
                    shouldFailValidation<Category> {
                        Category.create(name = "   ")
                    }.verify {
                        expect(Category::name, "   ", NotBlank)
                    }
                }
            }
        }

        describe("Updating a Category") {
            context("With valid data") {
                it("Should update category successfully") {
                    val category = category()

                    val updated = category.update("Updated Category")

                    updated.name shouldBe "Updated Category"
                    updated.id shouldBe category.id
                }
            }

            context("With invalid data") {
                it("Should fail when name is blank") {
                    val category = category()

                    shouldFailValidation<Category> {
                        category.update("")
                    }.verify {
                        expect(Category::name, "", NotBlank)
                    }
                }

                it("Should fail when name is whitespace only") {
                    val category = category()

                    shouldFailValidation<Category> {
                        category.update("   ")
                    }.verify {
                        expect(Category::name, "   ", NotBlank)
                    }
                }
            }
        }
    }
}
