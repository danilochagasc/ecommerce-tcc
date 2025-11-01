package com.br.danilo.tcc.order.core.domain.order

class OrderNotFoundException(
    val id: OrderId,
) : Exception("Order with id $id not found")

class OrderFailureException(
    val id: OrderId,
) : Exception("Order with id $id had error")
