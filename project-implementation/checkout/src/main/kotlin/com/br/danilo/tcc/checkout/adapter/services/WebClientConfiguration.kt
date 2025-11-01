package com.br.danilo.tcc.checkout.adapter.services

import com.br.danilo.tcc.checkout.adapter.services.stock.StockProperties
import kotlinx.serialization.ExperimentalSerializationApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(
    private val stockProperties: StockProperties,
) {
    @Bean
    @Qualifier("stockWebClient")
    @OptIn(ExperimentalSerializationApi::class)
    fun stockWebClient(): WebClient = ConnectionFactory.webClient(stockProperties, "stock-connection-provider")

    // Exemplo de como criar outro WebClient para outro serviço:
    // @Bean
    // @Qualifier("productWebClient")
    // @OptIn(ExperimentalSerializationApi::class)
    // fun productWebClient(@Autowired productProperties: ProductProperties): WebClient {
    //     return ConnectionFactory.webClient(productProperties, "product-connection-provider")
    // }
}
