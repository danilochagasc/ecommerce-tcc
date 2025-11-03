package com.br.danilo.tcc.order.core.application.order

import com.br.danilo.tcc.order.core.application.item.command.CreateItemCommand
import com.br.danilo.tcc.order.core.application.order.OrderServiceTestFixture.createOrderCommand
import com.br.danilo.tcc.order.core.application.order.OrderServiceTestFixture.updateStatusCommand
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.item.Item
import com.br.danilo.tcc.order.core.domain.item.ItemId
import com.br.danilo.tcc.order.core.domain.item.ItemRepository
import com.br.danilo.tcc.order.core.domain.order.Order
import com.br.danilo.tcc.order.core.domain.order.OrderId
import com.br.danilo.tcc.order.core.domain.order.OrderNotFoundException
import com.br.danilo.tcc.order.core.domain.order.OrderRepository
import com.br.danilo.tcc.order.core.domain.order.OrderStatusEnum
import com.br.danilo.tcc.order.core.domain.order.OrderTestFixture.order
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

class OrderServiceTest : DescribeSpec() {
    private val orderRepository = mockk<OrderRepository>()
    private val itemRepository = mockk<ItemRepository>()

    private val service =
        OrderService(
            orderRepository = orderRepository,
            itemRepository = itemRepository,
        )

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(orderRepository, itemRepository)
    }

    init {
        describe("Finding all orders") {
            it("Should return all orders") {
                val order1 = order()
                val order2 = order()
                val item1 = Item.create(orderId = order1.id, name = "Item 1", price = 50.0, quantity = 1)
                val item2 = Item.create(orderId = order2.id, name = "Item 2", price = 30.0, quantity = 2)

                coEvery { orderRepository.findAll() } returns listOf(order1, order2)
                coEvery { itemRepository.findAllByOrderId(order1.id) } returns listOf(item1)
                coEvery { itemRepository.findAllByOrderId(order2.id) } returns listOf(item2)

                val result = runBlocking { service.findAll() }

                result.size shouldBe 2
                coVerify { orderRepository.findAll() }
                coVerify { itemRepository.findAllByOrderId(order1.id) }
                coVerify { itemRepository.findAllByOrderId(order2.id) }
            }

            it("Should return empty list when no orders exist") {
                coEvery { orderRepository.findAll() } returns emptyList()

                val result = runBlocking { service.findAll() }

                result shouldBe emptyList()
                coVerify { orderRepository.findAll() }
            }
        }

        describe("Finding orders by account id") {
            context("Account has orders") {
                it("Should return orders for account") {
                    val accountId = AccountId()
                    val order1 = order(accountId = accountId)
                    val order2 = order(accountId = accountId)
                    val item1 = Item.create(orderId = order1.id, name = "Item 1", price = 50.0, quantity = 1)
                    val item2 = Item.create(orderId = order2.id, name = "Item 2", price = 30.0, quantity = 2)

                    coEvery { orderRepository.finAllByAccountId(accountId) } returns listOf(order1, order2)
                    coEvery { itemRepository.findAllByOrderId(order1.id) } returns listOf(item1)
                    coEvery { itemRepository.findAllByOrderId(order2.id) } returns listOf(item2)

                    val result = runBlocking { service.findAllByAccountId(accountId) }

                    result.size shouldBe 2
                    coVerify { orderRepository.finAllByAccountId(accountId) }
                    coVerify { itemRepository.findAllByOrderId(order1.id) }
                    coVerify { itemRepository.findAllByOrderId(order2.id) }
                }
            }

            context("Account has no orders") {
                it("Should return empty list") {
                    val accountId = AccountId()
                    coEvery { orderRepository.finAllByAccountId(accountId) } returns emptyList()

                    val result = runBlocking { service.findAllByAccountId(accountId) }

                    result shouldBe emptyList()
                    coVerify { orderRepository.finAllByAccountId(accountId) }
                }
            }
        }

        describe("Creating order") {
            context("With valid data") {
                it("Should create order successfully") {
                    val command = createOrderCommand()
                    coEvery { orderRepository.create(any()) } returns Unit
                    coEvery { itemRepository.create(any()) } returns Unit

                    val result = runBlocking { service.create(command) }

                    result shouldNotBe null
                    coVerify { orderRepository.create(any()) }
                    coVerify(exactly = command.items.size) { itemRepository.create(any()) }
                }

                it("Should create order with multiple items") {
                    val command = createOrderCommand(
                        items = listOf(
                            CreateItemCommand(
                                id = ItemId(),
                                name = "Item 1",
                                price = 50.0,
                                quantity = 2,
                            ),
                            CreateItemCommand(
                                id = ItemId(),
                                name = "Item 2",
                                price = 30.0,
                                quantity = 1,
                            ),
                        ),
                    )
                    coEvery { orderRepository.create(any()) } returns Unit
                    coEvery { itemRepository.create(any()) } returns Unit

                    val result = runBlocking { service.create(command) }

                    result shouldNotBe null
                    coVerify { orderRepository.create(any()) }
                    coVerify(exactly = 2) { itemRepository.create(any()) }
                }
            }
        }

        describe("Updating order status") {
            context("Order exists") {
                it("Should update order status successfully") {
                    val order = order()
                    val command = updateStatusCommand(id = order.id, status = OrderStatusEnum.PAID)
                    coEvery { orderRepository.findById(order.id) } returns order
                    coEvery { orderRepository.update(any()) } returns Unit

                    runBlocking { service.updateStatus(command) }

                    coVerify { orderRepository.findById(order.id) }
                    coVerify { orderRepository.update(any()) }
                }
            }

            context("Order does not exist") {
                it("Should throw OrderNotFoundException") {
                    val command = updateStatusCommand()
                    coEvery { orderRepository.findById(command.id) } returns null

                    shouldThrow<OrderNotFoundException> {
                        runBlocking { service.updateStatus(command) }
                    }

                    coVerify { orderRepository.findById(command.id) }
                    coVerify(exactly = 0) { orderRepository.update(any()) }
                }
            }
        }

        describe("Deleting order") {
            context("Order exists") {
                it("Should delete order successfully") {
                    val order = order()
                    coEvery { orderRepository.findById(order.id) } returns order
                    coEvery { orderRepository.delete(order.id) } returns Unit

                    runBlocking { service.delete(order.id) }

                    coVerify { orderRepository.findById(order.id) }
                    coVerify { orderRepository.delete(order.id) }
                }
            }

            context("Order does not exist") {
                it("Should throw OrderNotFoundException") {
                    val orderId = OrderId()
                    coEvery { orderRepository.findById(orderId) } returns null

                    shouldThrow<OrderNotFoundException> {
                        runBlocking { service.delete(orderId) }
                    }

                    coVerify { orderRepository.findById(orderId) }
                    coVerify(exactly = 0) { orderRepository.delete(any()) }
                }
            }
        }
    }
}

