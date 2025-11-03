package com.br.danilo.tcc.order.core.domain.item

import com.br.danilo.tcc.order.core.domain.order.OrderId

object ItemTestFixture {
    fun item(
        orderId: OrderId = OrderId(),
        name: String = "Item 1",
        price: Double = 50.0,
        quantity: Int = 2,
    ) = Item.create(
        orderId = orderId,
        name = name,
        price = price,
        quantity = quantity,
    )
}

