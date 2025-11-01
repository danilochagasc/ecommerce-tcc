package com.br.danilo.tcc.order.core.application.order.command

import com.br.danilo.tcc.order.core.application.item.command.CreateItemCommand
import com.br.danilo.tcc.order.core.domain.account.AccountId
import com.br.danilo.tcc.order.core.domain.paymentdetails.PaymentDetails

data class CreateOrderCommand(
    val accountId: AccountId,
    val items: List<CreateItemCommand>,
    val coupon: String?,
    val paymentDetails: PaymentDetails,
)
