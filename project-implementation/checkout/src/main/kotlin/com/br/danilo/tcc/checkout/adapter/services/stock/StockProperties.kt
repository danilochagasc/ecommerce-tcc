package com.br.danilo.tcc.checkout.adapter.services.stock

import com.br.danilo.tcc.checkout.adapter.services.ConnectionProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("stock")
data class StockProperties(
    override val baseUrl: String,
    override val connTimeout: Duration,
    override val connReadTimeout: Duration,
) : ConnectionProperties
