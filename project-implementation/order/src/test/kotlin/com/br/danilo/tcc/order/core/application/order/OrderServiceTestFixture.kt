package com.br.danilo.tcc.order.core.application.order

import com.br.danilo.tcc.order.core.application.item.command.CreateItemCommand
import com.br.danilo.tcc.order.core.application.order.command.CreateOrderCommand
import com.br.danilo.tcc.order.core.application.order.command.UpdateStatusCommand
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.item.ItemId
import com.br.danilo.tcc.order.core.domain.order.OrderId
import com.br.danilo.tcc.order.core.domain.order.OrderStatusEnum
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentDetails
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentTypeEnum

object OrderServiceTestFixture {
    fun createOrderCommand(
        accountId: AccountId = AccountId(),
        items: List<CreateItemCommand> = listOf(
            CreateItemCommand(
                id = ItemId(),
                name = "Item 1",
                price = 50.0,
                quantity = 2,
            ),
        ),
        coupon: String? = null,
        paymentDetails: PaymentDetails = PaymentDetails(paymentType = PaymentTypeEnum.PIX),
    ) = CreateOrderCommand(
        accountId = accountId,
        items = items,
        coupon = coupon,
        paymentDetails = paymentDetails,
    )

    fun updateStatusCommand(
        id: OrderId = OrderId(),
        status: OrderStatusEnum = OrderStatusEnum.PAID,
    ) = UpdateStatusCommand(
        id = id,
        status = status,
    )
}

