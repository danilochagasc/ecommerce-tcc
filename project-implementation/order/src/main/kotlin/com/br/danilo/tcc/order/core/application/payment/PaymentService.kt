package com.br.danilo.tcc.order.core.application.payment

interface PaymentService {
    suspend fun authorize(): String
}
