package com.br.danilo.tcc.order.core.domain.item

import com.br.danilo.tcc.order.core.domain.item.ItemTestFixture.item
import com.br.danilo.tcc.order.core.domain.order.OrderId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.valiktor.constraints.Greater
import org.valiktor.constraints.NotBlank
import org.valiktor.test.shouldFailValidation

class ItemTest : DescribeSpec() {
    init {
        describe("Creating an Item") {
            context("A valid one") {
                val item = item()

                it("Should be created properly") {
                    item.id shouldNotBe null
                    item.orderId shouldNotBe null
                    item.name shouldBe "Item 1"
                    item.price shouldBe 50.0
                    item.quantity shouldBe 2
                }
            }

            context("Invalid name") {
                it("Should fail when name is blank") {
                    shouldFailValidation<Item> {
                        Item.create(
                            orderId = OrderId(),
                            name = "",
                            price = 50.0,
                            quantity = 2,
                        )
                    }.verify {
                        expect(Item::name, "", NotBlank)
                    }
                }
            }

            context("Invalid price") {
                it("Should fail when price is zero") {
                    shouldFailValidation<Item> {
                        Item.create(
                            orderId = OrderId(),
                            name = "Item 1",
                            price = 0.0,
                            quantity = 2,
                        )
                    }.verify {
                        expect(Item::price, 0.0, Greater(0.0))
                    }
                }

                it("Should fail when price is negative") {
                    shouldFailValidation<Item> {
                        Item.create(
                            orderId = OrderId(),
                            name = "Item 1",
                            price = -10.0,
                            quantity = 2,
                        )
                    }.verify {
                        expect(Item::price, -10.0, Greater(0.0))
                    }
                }
            }

            context("Invalid quantity") {
                it("Should fail when quantity is zero") {
                    shouldFailValidation<Item> {
                        Item.create(
                            orderId = OrderId(),
                            name = "Item 1",
                            price = 50.0,
                            quantity = 0,
                        )
                    }.verify {
                        expect(Item::quantity, 0, Greater(0))
                    }
                }

                it("Should fail when quantity is negative") {
                    shouldFailValidation<Item> {
                        Item.create(
                            orderId = OrderId(),
                            name = "Item 1",
                            price = 50.0,
                            quantity = -5,
                        )
                    }.verify {
                        expect(Item::quantity, -5, Greater(0))
                    }
                }
            }
        }

        describe("Item creation with different values") {
            context("With valid values") {
                it("Should create item with custom values") {
                    val orderId = OrderId()
                    val item = item(
                        orderId = orderId,
                        name = "Custom Item",
                        price = 100.0,
                        quantity = 5,
                    )

                    item.orderId shouldBe orderId
                    item.name shouldBe "Custom Item"
                    item.price shouldBe 100.0
                    item.quantity shouldBe 5
                }
            }
        }
    }
}

