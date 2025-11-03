package com.br.danilo.tcc.order.core.domain.order

import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.item.Item
import com.br.danilo.tcc.order.core.domain.order.OrderTestFixture.order
import com.br.danilo.tcc.order.core.domain.order.OrderTestFixture.orderWithCreditCard
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentDetails
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentTypeEnum
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.valiktor.constraints.Greater
import org.valiktor.test.shouldFailValidation

class OrderTest : DescribeSpec() {
    init {
        describe("Creating an Order") {
            context("A valid one") {
                val order = order()

                it("Should be created properly") {
                    order.id shouldNotBe null
                    order.accountId shouldNotBe null
                    order.total shouldBe 100.0
                    order.items.size shouldBe 1
                    order.status shouldBe OrderStatusEnum.CREATED
                    order.paymentDetails shouldNotBe null
                    order.createdAt shouldNotBe null
                    order.updatedAt shouldNotBe null
                }
            }

            context("With coupon") {
                it("Should create order with coupon") {
                    val order = order(coupon = "DISCOUNT10")

                    order.coupon shouldBe "DISCOUNT10"
                }
            }

            context("With credit card payment") {
                it("Should create order with credit card payment details") {
                    val order = orderWithCreditCard()

                    order.paymentDetails.paymentType shouldBe PaymentTypeEnum.CREDIT
                    order.paymentDetails.card shouldNotBe null
                }
            }

            context("With debit card payment") {
                it("Should create order with debit card payment details") {
                    val order = order(
                        paymentDetails = PaymentDetails(
                            paymentType = PaymentTypeEnum.DEBIT,
                            card = PaymentDetails.Card(
                                cardNumber = "1234567890123456",
                                cardHolderName = "John Doe",
                                cardExpirationDate = kotlin.time.Instant.fromEpochSeconds(1735689600),
                                cardCvvNumber = "123",
                            ),
                        ),
                    )

                    order.paymentDetails.paymentType shouldBe PaymentTypeEnum.DEBIT
                    order.paymentDetails.card shouldNotBe null
                }
            }

            context("Invalid payment details for credit/debit") {
                it("Should fail when credit card payment has null card") {
                    shouldThrow<org.valiktor.ConstraintViolationException> {
                        Order.create(
                            id = OrderId(),
                            accountId = AccountId(),
                            items = listOf(
                                Item.create(
                                    orderId = OrderId(),
                                    name = "Item 1",
                                    price = 50.0,
                                    quantity = 2,
                                ),
                            ),
                            coupon = null,
                            paymentDetails = PaymentDetails(
                                paymentType = PaymentTypeEnum.CREDIT,
                                card = null,
                            ),
                        )
                    }
                }

                it("Should fail when debit card payment has null card") {
                    shouldThrow<org.valiktor.ConstraintViolationException> {
                        Order.create(
                            id = OrderId(),
                            accountId = AccountId(),
                            items = listOf(
                                Item.create(
                                    orderId = OrderId(),
                                    name = "Item 1",
                                    price = 50.0,
                                    quantity = 2,
                                ),
                            ),
                            coupon = null,
                            paymentDetails = PaymentDetails(
                                paymentType = PaymentTypeEnum.DEBIT,
                                card = null,
                            ),
                        )
                    }
                }
            }

            context("Invalid total") {
                it("Should fail when total is zero (no items)") {
                    shouldFailValidation<Order> {
                        Order.create(
                            id = OrderId(),
                            accountId = AccountId(),
                            items = emptyList(),
                            coupon = null,
                            paymentDetails = PaymentDetails(paymentType = PaymentTypeEnum.PIX),
                        )
                    }.verify {
                        expect(Order::total, 0.0, Greater(0.0))
                    }
                }

                it("Should fail when total is negative (item with negative price)") {
                    shouldFailValidation<Item> {
                        Item.create(
                            orderId = OrderId(),
                            name = "Item 1",
                            price = -10.0,
                            quantity = 1,
                        )
                    }.verify {
                        expect(Item::price, -10.0, Greater(0.0))
                    }
                }
            }
        }

        describe("Inserting items into an Order") {
            context("With valid items") {
                it("Should insert items successfully") {
                    val order = order()
                    val newItems = listOf(
                        Item.create(orderId = order.id, name = "Item 2", price = 30.0, quantity = 1),
                        Item.create(orderId = order.id, name = "Item 3", price = 20.0, quantity = 2),
                    )

                    val updated = order.insertItems(newItems)

                    updated.items shouldBe newItems
                    updated.items.size shouldBe 2
                    updated.id shouldBe order.id
                    updated.accountId shouldBe order.accountId
                }
            }
        }

        describe("Updating order status") {
            context("With valid status") {
                it("Should update status successfully") {
                    val order = order()

                    val updated = order.updateStatus(OrderStatusEnum.PAID)

                    updated.status shouldBe OrderStatusEnum.PAID
                    updated.id shouldBe order.id
                    updated.accountId shouldBe order.accountId
                }
            }

            context("Multiple status updates") {
                it("Should update status multiple times") {
                    val order = order()
                    val pending = order.updateStatus(OrderStatusEnum.PENDING_PAYMENT)
                    val paid = pending.updateStatus(OrderStatusEnum.PAID)

                    paid.status shouldBe OrderStatusEnum.PAID
                    paid.id shouldBe order.id
                    pending.status shouldBe OrderStatusEnum.PENDING_PAYMENT
                }
            }
        }

        describe("Cancelling an Order") {
            context("With valid order") {
                it("Should cancel order successfully") {
                    val order = order()

                    val cancelled = order.cancel()

                    cancelled.status shouldBe OrderStatusEnum.CANCELLED
                    cancelled.id shouldBe order.id
                    cancelled.accountId shouldBe order.accountId
                    cancelled.items shouldBe order.items
                }
            }
        }
    }
}

