package com.br.danilo.tcc.order.core.application.order.command

import com.br.danilo.tcc.order.core.domain.order.OrderId
import com.br.danilo.tcc.order.core.domain.order.OrderStatusEnum

data class UpdateStatusCommand(
    val id: OrderId,
    val status: OrderStatusEnum,
)
