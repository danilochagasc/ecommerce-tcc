package com.br.danilo.tcc.order.core.application.item.command

import com.br.danilo.tcc.order.core.domain.item.ItemId

data class CreateItemCommand(
    val id: ItemId,
    val name: String,
    val price: Double,
    val quantity: Int,
)
