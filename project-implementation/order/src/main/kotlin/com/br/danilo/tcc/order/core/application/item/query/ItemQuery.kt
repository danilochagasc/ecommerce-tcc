package com.br.danilo.tcc.order.core.application.item.query

import com.br.danilo.tcc.order.core.domain.item.Item
import com.br.danilo.tcc.order.core.domain.item.ItemId
import com.br.danilo.tcc.order.core.domain.order.OrderId

data class ItemQuery(
    val id: ItemId,
    val orderId: OrderId,
    val name: String,
    val price: Double,
    val quantity: Int,
)

fun Item.toQuery() =
    ItemQuery(
        id = this.id,
        orderId = this.orderId,
        name = this.name,
        price = this.price,
        quantity = this.quantity,
    )
