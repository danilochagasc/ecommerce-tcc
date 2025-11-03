package com.br.danilo.tcc.order.core.domain.order

import com.br.danilo.tcc.order.core.domain.AggregateId
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.item.Item
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentDetails
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentTypeEnum
import org.valiktor.functions.isGreaterThan
import org.valiktor.functions.isNotNull
import org.valiktor.validate
import kotlin.time.Clock.System.now
import kotlin.time.Instant

typealias OrderId = AggregateId

data class Order(
    val id: OrderId,
    val accountId: AccountId,
    val total: Double,
    val coupon: String?,
    val items: List<Item>,
    val status: OrderStatusEnum,
    val paymentDetails: PaymentDetails,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    private fun validate() {
        validate(this) {
            validate(Order::id).isNotNull()
            validate(Order::accountId).isNotNull()
            validate(Order::total).isNotNull().isGreaterThan(0.0)
            validate(Order::items).isNotNull()
            validate(Order::status).isNotNull()
            validate(Order::createdAt).isNotNull()
            validate(Order::updatedAt).isNotNull()
            when (paymentDetails.paymentType) {
                PaymentTypeEnum.CREDIT, PaymentTypeEnum.DEBIT -> {
                    validate(it.paymentDetails) {
                        validate(PaymentDetails::card).isNotNull()
                    }
                }
                else -> Unit
            }
        }
    }

    companion object {
        fun create(
            id: OrderId,
            accountId: AccountId,
            items: List<Item>,
            coupon: String?,
            paymentDetails: PaymentDetails,
        ) = Order(
            id = id,
            accountId = accountId,
            total = items.sumOf { it.price * it.quantity },
            coupon = coupon,
            items = items,
            status = OrderStatusEnum.CREATED,
            createdAt = now(),
            updatedAt = now(),
            paymentDetails = paymentDetails,
        ).apply { validate() }
    }

    fun insertItems(items: List<Item>) = copy(items = items)

    fun updateStatus(status: OrderStatusEnum) = copy(status = status, updatedAt = now())

    fun cancel() = copy(status = OrderStatusEnum.CANCELLED)
}
