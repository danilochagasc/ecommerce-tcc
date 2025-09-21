package com.br.danilo.tcc.checkout.adapter.redis.cart

import com.br.danilo.tcc.checkout.core.domain.cart.Cart
import com.br.danilo.tcc.checkout.core.domain.cart.CartId
import com.br.danilo.tcc.checkout.core.domain.cart.CartRepository
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class CartRedisRepository(
    private val reactiveStringRedisTemplate: ReactiveStringRedisTemplate,
    private val objectMapper: ObjectMapper,
) : CartRepository {
    private val operations: ReactiveValueOperations<String, String> = reactiveStringRedisTemplate.opsForValue()

    private fun key(cartId: CartId) = "cart:$cartId"

    override suspend fun findById(cartId: CartId): Cart? =
        operations.get(key(cartId)).awaitSingleOrNull()?.let {
            objectMapper.readValue(it, CartRedisTemplate::class.java)
        }?.toDomain()

    override suspend fun createOrUpdate(cart: Cart): Cart {
        operations
            .set(
                key(cart.id),
                objectMapper.writeValueAsString(cart.toRedisTemplate()),
                Duration.ofMinutes(15),
            ).awaitSingleOrNull()
        return cart
    }

    override suspend fun delete(cartId: CartId) {
        operations.delete(key(cartId)).awaitSingleOrNull()
    }
}