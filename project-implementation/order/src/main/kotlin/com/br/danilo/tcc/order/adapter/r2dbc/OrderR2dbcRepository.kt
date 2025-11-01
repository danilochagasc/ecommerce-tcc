package com.br.danilo.tcc.order.adapter.r2dbc

import com.br.danilo.tcc.order.adapter.r2dbc.queries.OrderSqlQueries.deleteOrder
import com.br.danilo.tcc.order.adapter.r2dbc.queries.OrderSqlQueries.insertOrder
import com.br.danilo.tcc.order.adapter.r2dbc.queries.OrderSqlQueries.selectOrder
import com.br.danilo.tcc.order.adapter.r2dbc.queries.OrderSqlQueries.updateOrder
import com.br.danilo.tcc.order.adapter.r2dbc.queries.OrderSqlQueries.whereAccountId
import com.br.danilo.tcc.order.adapter.r2dbc.queries.OrderSqlQueries.whereId
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.order.Order
import com.br.danilo.tcc.order.core.domain.order.OrderId
import com.br.danilo.tcc.order.core.domain.order.OrderRepository
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentDetails
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.toList
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@Repository
class OrderR2dbcRepository(
    private val db: DatabaseClient,
) : OrderRepository {
    override suspend fun findAll(): List<Order> =
        selectOrder()
            .run {
                db
                    .sql(this)
                    .map { row, _ -> row.toOrder() }
                    .flow()
                    .toList()
            }

    override suspend fun finAllByAccountId(accountId: AccountId): List<Order> =
        selectOrder()
            .where(whereAccountId(accountId))
            .run {
                db
                    .sql(this)
                    .bind("accountId", accountId.toUUID())
                    .map { row, _ -> row.toOrder() }
                    .flow()
                    .toList()
            }

    override suspend fun findById(id: OrderId): Order? =
        selectOrder()
            .where(whereId(id))
            .run {
                db
                    .sql(this)
                    .bindIfNotNull("id", id.toUUID())
                    .map { row, _ ->
                        row.toOrder()
                    }.awaitOneOrNull()
            }

    override suspend fun create(order: Order) {
        db
            .sql(insertOrder())
            .bind("id", order.id.toUUID())
            .bind("accountId", order.accountId.toUUID())
            .bind("total", order.total)
            .bindOrNull("coupon", order.coupon)
            .bind("status", order.status.name)
            .bind("paymentType", order.paymentDetails.paymentType.name)
            .bind("createdAt", order.createdAt.toJavaInstant())
            .bind("updatedAt", order.updatedAt.toJavaInstant())
            .await()
    }

    override suspend fun update(order: Order) {
        db
            .sql(updateOrder())
            .bind("id", order.id.toUUID())
            .bind("total", order.total)
            .bindOrNull("coupon", order.coupon)
            .bind("status", order.status.name)
            .bind("payment_type", order.paymentDetails.paymentType.name)
            .bind("updated_at", order.updatedAt)
            .await()
    }

    override suspend fun delete(id: OrderId) {
        db
            .sql(deleteOrder())
            .bind("id", id.toUUID())
            .await()
    }

    private fun Row.toOrder() =
        Order(
            id = OrderId(this.get<UUID>("id")),
            accountId = AccountId(this.get<UUID>("account_id")),
            total = this.get<Double>("total"),
            coupon = this.getOrNull<String>("coupon"),
            items = emptyList(),
            status = enumValueOf(this.get<String>("status")),
            paymentDetails =
                PaymentDetails(
                    paymentType = enumValueOf(this.get<String>("payment_type")),
                    card = null,
                ),
            createdAt = this.get<Instant>("created_at").toKotlinInstant(),
            updatedAt = this.get<Instant>("updated_at").toKotlinInstant(),
        )
}
