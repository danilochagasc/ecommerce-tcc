package com.br.danilo.tcc.order.adapter.r2dbc

import com.br.danilo.tcc.order.adapter.r2dbc.queries.ItemSqlQueries.insertItem
import com.br.danilo.tcc.order.adapter.r2dbc.queries.ItemSqlQueries.selectItem
import com.br.danilo.tcc.order.adapter.r2dbc.queries.ItemSqlQueries.whereOrderId
import com.br.danilo.tcc.order.core.domain.item.Item
import com.br.danilo.tcc.order.core.domain.item.ItemId
import com.br.danilo.tcc.order.core.domain.item.ItemRepository
import com.br.danilo.tcc.order.core.domain.order.OrderId
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.toList
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ItemR2dbcRepository(
    private val db: DatabaseClient,
) : ItemRepository {
    override suspend fun findAll(): List<Item> =
        selectItem()
            .run {
                db
                    .sql(this)
                    .map { row, _ -> row.toItem() }
                    .flow()
                    .toList()
            }

    override suspend fun findAllByOrderId(orderId: OrderId): List<Item> =
        selectItem()
            .where(whereOrderId(orderId))
            .run {
                db
                    .sql(this)
                    .bind("orderId", orderId.toUUID())
                    .map { row, _ -> row.toItem() }
                    .flow()
                    .toList()
            }

    override suspend fun create(item: Item) {
        db
            .sql(insertItem())
            .bind("id", item.id.toUUID())
            .bind("orderId", item.orderId.toUUID())
            .bind("name", item.name)
            .bind("price", item.price)
            .bind("quantity", item.quantity)
            .await()
    }

    private fun Row.toItem() =
        Item(
            id = ItemId(this.get<UUID>("id")),
            orderId = OrderId(this.get<UUID>("order_id")),
            name = this.get<String>("name"),
            price = this.get<Double>("price"),
            quantity = this.get<Int>("quantity"),
        )
}
