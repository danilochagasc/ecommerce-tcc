package com.br.danilo.tcc.order.core.domain.order

import com.br.danilo.tcc.order.core.domain.account.AccountId

interface OrderRepository {
    suspend fun findAll(): List<Order>

    suspend fun finAllByAccountId(accountId: AccountId): List<Order>

    suspend fun findById(id: OrderId): Order?

    suspend fun create(order: Order)

    suspend fun update(order: Order)

    suspend fun delete(id: OrderId)
}
