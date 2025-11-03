package com.br.danilo.tcc.order.adapter.r2dbc

import com.br.danilo.tcc.order.core.domain.item.Item
import com.br.danilo.tcc.order.core.domain.item.ItemId
import com.br.danilo.tcc.order.core.domain.order.OrderId
import java.util.UUID

object ItemR2dbcRepositoryTestFixture {
    private val itemId = ItemId(UUID.fromString("770e8400-e29b-41d4-a716-446655440010"))
    private val anotherItemId = ItemId(UUID.fromString("880e8400-e29b-41d4-a716-446655440011"))
    private val yetAnotherItemId = ItemId(UUID.fromString("990e8400-e29b-41d4-a716-446655440012"))
    private val orderId = OrderId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
    private val anotherOrderId = OrderId(UUID.fromString("660e8400-e29b-41d4-a716-446655440001"))

    fun itemId() = itemId

    fun anotherItemId() = anotherItemId

    fun orderId() = orderId

    fun anotherOrderId() = anotherOrderId

    fun item() =
        Item(
            id = itemId,
            orderId = orderId,
            name = "Item 1",
            price = 50.0,
            quantity = 2,
        )

    fun anotherItem() =
        Item(
            id = anotherItemId,
            orderId = orderId,
            name = "Item 2",
            price = 50.0,
            quantity = 1,
        )

    fun itemFromAnotherOrder() =
        Item(
            id = yetAnotherItemId,
            orderId = anotherOrderId,
            name = "Item 3",
            price = 100.0,
            quantity = 2,
        )

    fun itemUpdated() =
        Item(
            id = itemId,
            orderId = orderId,
            name = "Updated Item 1",
            price = 60.0,
            quantity = 3,
        )
}

