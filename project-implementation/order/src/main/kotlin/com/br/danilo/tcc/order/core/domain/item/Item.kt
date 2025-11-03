package com.br.danilo.tcc.order.core.domain.item

import com.br.danilo.tcc.order.core.domain.AggregateId
import com.br.danilo.tcc.order.core.domain.order.OrderId
import org.valiktor.functions.isGreaterThan
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.validate

typealias ItemId = AggregateId

data class Item(
    val id: ItemId,
    val orderId: OrderId,
    val name: String,
    val price: Double,
    val quantity: Int,
) {
    private fun validate() {
        validate(this) {
            validate(Item::id).isNotNull()
            validate(Item::orderId).isNotNull()
            validate(Item::name).isNotNull().isNotBlank()
            validate(Item::price).isNotNull().isGreaterThan(0.0)
            validate(Item::quantity).isGreaterThan(0)
        }
    }

    companion object {
        fun create(
            orderId: OrderId,
            name: String,
            price: Double,
            quantity: Int,
        ) = Item(
            id = ItemId(),
            orderId = orderId,
            name = name,
            price = price,
            quantity = quantity,
        ).apply { validate() }
    }
}
