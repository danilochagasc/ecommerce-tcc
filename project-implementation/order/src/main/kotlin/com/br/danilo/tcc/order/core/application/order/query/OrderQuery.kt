package com.br.danilo.tcc.order.core.application.order.query

import com.br.danilo.tcc.order.core.application.item.query.ItemQuery
import com.br.danilo.tcc.order.core.application.item.query.toQuery
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.order.Order
import com.br.danilo.tcc.order.core.domain.order.OrderId

data class OrderQuery(
    val id: OrderId,
    val accountId: AccountId,
    val price: Double,
    val coupon: String?,
    val items: List<ItemQuery>,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
)

fun Order.toQuery() =
    OrderQuery(
        id = this.id,
        accountId = this.accountId,
        price = this.total,
        coupon = this.coupon,
        items = this.items.map { it.toQuery() },
        status = this.status.name,
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString(),
    )
