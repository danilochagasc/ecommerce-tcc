package com.br.danilo.tcc.order.core.application.order

import com.br.danilo.tcc.order.core.application.order.command.CreateOrderCommand
import com.br.danilo.tcc.order.core.application.order.command.UpdateStatusCommand
import com.br.danilo.tcc.order.core.application.order.query.OrderQuery
import com.br.danilo.tcc.order.core.application.order.query.toQuery
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.item.Item
import com.br.danilo.tcc.order.core.domain.item.ItemRepository
import com.br.danilo.tcc.order.core.domain.order.Order
import com.br.danilo.tcc.order.core.domain.order.OrderId
import com.br.danilo.tcc.order.core.domain.order.OrderNotFoundException
import com.br.danilo.tcc.order.core.domain.order.OrderRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val itemRepository: ItemRepository,
) {
    suspend fun findAll(): List<OrderQuery> =
        coroutineScope {
            val orders = orderRepository.findAll()

            orders
                .map { order ->
                    async {
                        order.insertItems(itemRepository.findAllByOrderId(order.id))
                    }
                }.awaitAll()
                .map { it.toQuery() }
        }

    suspend fun findAllByAccountId(accountId: AccountId): List<OrderQuery> =
        coroutineScope {
            val orders = orderRepository.finAllByAccountId(accountId)

            orders
                .map { order ->
                    async {
                        order.insertItems(itemRepository.findAllByOrderId(order.id))
                    }
                }.awaitAll()
                .map { it.toQuery() }
        }

    suspend fun create(command: CreateOrderCommand): OrderId {
        val orderId = OrderId()
        val order =
            Order.create(
                id = orderId,
                accountId = command.accountId,
                items =
                    command.items.map {
                        Item.create(
                            orderId = orderId,
                            name = it.name,
                            price = it.price,
                            quantity = it.quantity,
                        )
                    },
                coupon = command.coupon,
                paymentDetails = command.paymentDetails,
            )

        coroutineScope {
            orderRepository.create(order)
            order.items
                .map {
                    async {
                        itemRepository.create(it)
                    }
                }.awaitAll()
        }

        return orderId
    }

    suspend fun updateStatus(command: UpdateStatusCommand) {
        coroutineScope {
            val order = orderRepository.findById(command.id) ?: throw OrderNotFoundException(command.id)

            orderRepository.update(order.updateStatus(command.status))
        }
    }

    suspend fun delete(id: OrderId) {
        coroutineScope {
            val order = orderRepository.findById(id) ?: throw OrderNotFoundException(id)

            orderRepository.delete(order.id)
        }
    }
}
