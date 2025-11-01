package com.br.danilo.tcc.order.adapter.r2dbc.queries

import com.br.danilo.tcc.order.core.domain.item.ItemId
import com.br.danilo.tcc.order.core.domain.order.OrderId

object ItemSqlQueries {
    fun selectItem() =
        """
        SELECT *
            FROM item
         WHERE 1 = 1
    """

    fun whereId(id: ItemId?) =
        id?.let {
            """
           AND id = :id
        """
        }

    fun whereOrderId(orderId: OrderId?) =
        orderId?.let {
            """
           AND order_id = :orderId
        """
        }

    fun insertItem() =
        """
            INSERT INTO item(id, order_id, name, price, quantity)
            VALUES(:id, :orderId, :name, :price, :quantity)
        """
}
