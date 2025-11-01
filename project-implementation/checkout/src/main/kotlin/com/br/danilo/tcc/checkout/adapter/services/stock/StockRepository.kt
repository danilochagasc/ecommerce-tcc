package com.br.danilo.tcc.checkout.adapter.services.stock

import com.br.danilo.tcc.checkout.core.domain.cart.CartItem
import com.br.danilo.tcc.checkout.core.domain.cart.ProductId
import com.br.danilo.tcc.checkout.core.domain.stock.StockRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.awaitBody

@Service
class StockRepository(
    @Qualifier("stockWebClient")
    private val stockWebClient: WebClient,
) : StockRepository {
    override suspend fun findById(productId: ProductId): CartItem? =
        try {
            stockWebClient
                .get()
                .uri("/$productId")
                .retrieve()
                .awaitBody<Product>()
                .toCartItem()
        } catch (ex: Exception) {
            throw ex
        }

    override suspend fun decreaseQuantity(
        productId: ProductId,
        amount: Int,
    ) {
        try {
            stockWebClient
                .put()
                .uri("/$productId/decrease/$amount")
                .retrieve()
                .awaitBodilessEntity()
                .statusCode
        } catch (ex: Exception) {
            throw ex
        }
    }
}
