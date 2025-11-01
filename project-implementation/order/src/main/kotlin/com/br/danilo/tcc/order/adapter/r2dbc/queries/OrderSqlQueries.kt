package com.br.danilo.tcc.order.adapter.r2dbc.queries

import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.order.OrderId

object OrderSqlQueries {
    fun selectOrder() =
        """
        SELECT *
            FROM "order"
         WHERE 1 = 1
        """

    fun whereId(id: OrderId?) =
        id?.let {
            """
           AND id = :id
        """
        }

    fun whereAccountId(accountId: AccountId?) =
        accountId?.let {
            """
           AND account_id = :accountId
        """
        }

    fun insertOrder() =
        """
            INSERT INTO "order"(id, account_id, total, coupon, status, payment_type, created_at, updated_at)
            VALUES (:id, :accountId, :total, :coupon, :status, :paymentType, :createdAt, :updatedAt)
        """

    fun updateOrder() =
        """
            UPDATE "order"
               SET total = :total,
                   coupon = :coupon,
                   status = :status,
                   payment_type = :paymentType,
                   updated_at = :updated_at,
             WHERE id = :id
        """

    fun deleteOrder() =
        """id
            DELETE FROM "order"
             WHERE id = :id
        """
}
