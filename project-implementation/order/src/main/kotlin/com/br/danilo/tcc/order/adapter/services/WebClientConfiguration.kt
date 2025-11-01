package com.br.danilo.tcc.order.adapter.services

import com.br.danilo.tcc.order.adapter.services.payment.PaymentProperties
import kotlinx.serialization.ExperimentalSerializationApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(
    private val paymentProperties: PaymentProperties,
) {
    @Bean
    @Qualifier("paymentWebClient")
    @OptIn(ExperimentalSerializationApi::class)
    fun paymentWebClient(): WebClient = ConnectionFactory.webClient(paymentProperties, "payment-connection-provider")

    // Exemplo de como criar outro WebClient para outro serviço:
    // @Bean
    // @Qualifier("productWebClient")
    // @OptIn(ExperimentalSerializationApi::class)
    // fun productWebClient(@Autowired productProperties: ProductProperties): WebClient {
    //     return ConnectionFactory.webClient(productProperties, "product-connection-provider")
    // }
}
