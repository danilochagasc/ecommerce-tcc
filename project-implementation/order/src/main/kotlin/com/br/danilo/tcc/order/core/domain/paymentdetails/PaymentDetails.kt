package com.br.danilo.tcc.order.core.domain.paymentdetails

import kotlin.time.Instant

data class PaymentDetails(
    val paymentType: PaymentTypeEnum,
    val card: Card? = null,
) {
    data class Card(
        val cardNumber: String,
        val cardHolderName: String,
        val cardExpirationDate: Instant,
        val cardCvvNumber: String,
    )
}
