package com.br.danilo.tcc.order.adapter.r2dbc

import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.order.Order
import com.br.danilo.tcc.order.core.domain.order.OrderId
import com.br.danilo.tcc.order.core.domain.order.OrderStatusEnum
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentDetails
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentTypeEnum
import java.util.UUID

object OrderR2dbcRepositoryTestFixture {
    private val orderId = OrderId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
    private val anotherOrderId = OrderId(UUID.fromString("660e8400-e29b-41d4-a716-446655440001"))
    private val accountId = AccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
    private val anotherAccountId = AccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))

    fun orderId() = orderId

    fun anotherOrderId() = anotherOrderId

    fun accountId() = accountId

    fun order() =
        Order(
            id = orderId,
            accountId = accountId,
            total = 150.0,
            coupon = "DISCOUNT10",
            items = emptyList(),
            status = OrderStatusEnum.CREATED,
            paymentDetails = PaymentDetails(paymentType = PaymentTypeEnum.PIX),
            createdAt = kotlin.time.Instant.parse("2024-01-01T10:00:00Z"),
            updatedAt = kotlin.time.Instant.parse("2024-01-01T10:00:00Z"),
        )

    fun anotherOrder() =
        Order(
            id = anotherOrderId,
            accountId = anotherAccountId,
            total = 200.0,
            coupon = null,
            items = emptyList(),
            status = OrderStatusEnum.PAID,
            paymentDetails = PaymentDetails(paymentType = PaymentTypeEnum.CREDIT),
            createdAt = kotlin.time.Instant.parse("2024-01-02T11:00:00Z"),
            updatedAt = kotlin.time.Instant.parse("2024-01-02T11:30:00Z"),
        )

    fun orderUpdated() =
        Order(
            id = orderId,
            accountId = accountId,
            total = 180.0,
            coupon = "NEWDISCOUNT",
            items = emptyList(),
            status = OrderStatusEnum.PENDING_PAYMENT,
            paymentDetails = PaymentDetails(paymentType = PaymentTypeEnum.PIX),
            createdAt = kotlin.time.Instant.parse("2024-01-01T10:00:00Z"),
            updatedAt = kotlin.time.Instant.parse("2024-01-01T10:30:00Z"),
        )
}

