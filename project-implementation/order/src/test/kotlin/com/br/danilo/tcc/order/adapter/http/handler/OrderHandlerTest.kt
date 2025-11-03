package com.br.danilo.tcc.order.adapter.http.handler

import com.br.danilo.tcc.order.adapter.http.handler.OrderHandlerTestFixture.anotherOrderQuery
import com.br.danilo.tcc.order.adapter.http.handler.OrderHandlerTestFixture.orderQuery
import com.br.danilo.tcc.order.adapter.router.createWebTestClient
import com.br.danilo.tcc.order.core.application.order.OrderService
import com.br.danilo.tcc.order.core.application.order.OrderServiceTestFixture.createOrderCommand
import com.br.danilo.tcc.order.core.application.order.query.OrderQuery
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.order.OrderId
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest
class OrderHandlerTest(
    applicationContext: ApplicationContext,
) : DescribeSpec() {
    @MockkBean
    private lateinit var orderService: OrderService

    private val webClient = createWebTestClient(applicationContext)

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        clearMocks(orderService)
    }

    init {
        describe("Finding all orders") {
            it("Should return all orders") {
                val orders = listOf(orderQuery(), anotherOrderQuery())
                coEvery { orderService.findAll() } returns orders

                webClient
                    .get()
                    .uri("/order")
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(APPLICATION_JSON)
                    .expectBody<List<OrderQuery>>()

                coVerify { orderService.findAll() }
                confirmVerified(orderService)
            }
        }

        describe("Finding orders by account id") {
            it("Should return orders for account") {
                val accountId = AccountId()
                val orders = listOf(orderQuery())
                coEvery { orderService.findAllByAccountId(accountId) } returns orders

                webClient
                    .get()
                    .uri("/order/account/$accountId")
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(APPLICATION_JSON)
                    .expectBody<List<OrderQuery>>()

                coVerify { orderService.findAllByAccountId(accountId) }
                confirmVerified(orderService)
            }
        }

        describe("Creating order") {
            it("Should create order and return 201") {
                val command = createOrderCommand()
                val orderId = OrderId()

                coEvery { orderService.create(any()) } returns orderId

                webClient
                    .post()
                    .uri("/order")
                    .contentType(APPLICATION_JSON)
                    .bodyValue(command)
                    .exchange()
                    .expectStatus()
                    .isCreated

                coVerify { orderService.create(any()) }
                confirmVerified(orderService)
            }
        }

        describe("Deleting order") {
            it("Should delete order and return 204") {
                val orderId = OrderId()

                coEvery { orderService.delete(orderId) } returns Unit

                webClient
                    .delete()
                    .uri("/order/$orderId")
                    .exchange()
                    .expectStatus()
                    .isNoContent
                    .expectBody()
                    .isEmpty

                coVerify { orderService.delete(eq(orderId)) }
                confirmVerified(orderService)
            }
        }
    }
}

