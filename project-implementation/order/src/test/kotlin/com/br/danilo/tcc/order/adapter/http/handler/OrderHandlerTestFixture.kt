package com.br.danilo.tcc.order.adapter.http.handler

import com.br.danilo.tcc.order.core.application.item.query.ItemQuery
import com.br.danilo.tcc.order.core.application.order.query.OrderQuery
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.item.ItemId
import com.br.danilo.tcc.order.core.domain.order.OrderId

object OrderHandlerTestFixture {
    fun orderQuery() =
        OrderQuery(
            id = OrderId(),
            accountId = AccountId(),
            price = 150.0,
            coupon = "DISCOUNT10",
            items = listOf(
                ItemQuery(
                    id = ItemId(),
                    orderId = OrderId(),
                    name = "Item 1",
                    price = 50.0,
                    quantity = 2,
                ),
            ),
            status = "CREATED",
            createdAt = "2024-01-01T10:00:00Z",
            updatedAt = "2024-01-01T10:00:00Z",
        )

    fun anotherOrderQuery() =
        OrderQuery(
            id = OrderId(),
            accountId = AccountId(),
            price = 200.0,
            coupon = null,
            items = listOf(
                ItemQuery(
                    id = ItemId(),
                    orderId = OrderId(),
                    name = "Item 2",
                    price = 100.0,
                    quantity = 2,
                ),
            ),
            status = "PAID",
            createdAt = "2024-01-02T11:00:00Z",
            updatedAt = "2024-01-02T11:30:00Z",
        )
}

