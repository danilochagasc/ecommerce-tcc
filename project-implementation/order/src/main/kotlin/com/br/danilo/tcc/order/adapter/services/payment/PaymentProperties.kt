package com.br.danilo.tcc.order.adapter.services.payment

import com.br.danilo.tcc.order.adapter.services.ConnectionProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("payment")
data class PaymentProperties(
    override val baseUrl: String,
    override val connTimeout: Duration,
    override val connReadTimeout: Duration,
) : ConnectionProperties
