package com.br.danilo.tcc.order.core.domain.item

import com.br.danilo.tcc.order.core.domain.order.OrderId

interface ItemRepository {
    suspend fun findAll(): List<Item>

    suspend fun findAllByOrderId(orderId: OrderId): List<Item>

    suspend fun create(item: Item)
}
