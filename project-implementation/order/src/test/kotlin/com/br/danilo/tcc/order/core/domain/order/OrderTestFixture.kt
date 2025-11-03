package com.br.danilo.tcc.order.core.domain.order

import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.item.Item
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentDetails
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentTypeEnum

object OrderTestFixture {
    fun order(
        id: OrderId = OrderId(),
        accountId: AccountId = AccountId(),
        items: List<Item>? = null,
        coupon: String? = null,
        paymentDetails: PaymentDetails = PaymentDetails(paymentType = PaymentTypeEnum.PIX),
    ) = Order.create(
        id = id,
        accountId = accountId,
        items = items ?: listOf(
            Item.create(
                orderId = id,
                name = "Item 1",
                price = 50.0,
                quantity = 2,
            ),
        ),
        coupon = coupon,
        paymentDetails = paymentDetails,
    )

    fun orderWithCreditCard(
        id: OrderId = OrderId(),
        accountId: AccountId = AccountId(),
        items: List<Item>? = null,
        coupon: String? = null,
    ) = Order.create(
        id = id,
        accountId = accountId,
        items = items ?: listOf(
            Item.create(
                orderId = id,
                name = "Item 1",
                price = 50.0,
                quantity = 2,
            ),
        ),
        coupon = coupon,
        paymentDetails = PaymentDetails(
            paymentType = PaymentTypeEnum.CREDIT,
            card = PaymentDetails.Card(
                cardNumber = "1234567890123456",
                cardHolderName = "John Doe",
                cardExpirationDate = kotlin.time.Instant.fromEpochSeconds(1735689600), // 2025-01-01
                cardCvvNumber = "123",
            ),
        ),
    )

}

