package com.br.danilo.tcc.checkout.adapter.redis.coupon

import com.br.danilo.tcc.checkout.core.domain.coupon.Coupon
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponRepository
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Repository
import kotlin.time.toJavaDuration

@Repository
class CouponRedisRepository(
    private val reactiveStringRedisTemplate: ReactiveStringRedisTemplate,
    private val objectMapper: ObjectMapper,
) : CouponRepository {
    private val operations: ReactiveValueOperations<String, String> = reactiveStringRedisTemplate.opsForValue()

    private fun key(code: String) = "coupon:$code"

    override suspend fun findAll(): List<Coupon> =
        reactiveStringRedisTemplate
            .scan()
            .filter { it.startsWith("coupon:") }
            .flatMap { key -> operations.get(key) }
            .map { json -> objectMapper.readValue(json, CouponRedisTemplate::class.java).toDomain()}
            .collectList()
            .awaitSingle()

    override suspend fun findByCode(code: String): Coupon? =
        operations.get(key(code)).awaitSingleOrNull()?.let {
            objectMapper.readValue(it, CouponRedisTemplate::class.java)
        }?.toDomain()

    override suspend fun createOrUpdate(coupon: Coupon): Coupon {
        operations
            .set(
                key(coupon.code),
                objectMapper.writeValueAsString(coupon.toRedisTemplate()),
                coupon.getTimeToLive().toJavaDuration(),
            ).awaitSingle()
        return coupon
    }

    override suspend fun delete(code: String) {
        operations.delete(key(code)).awaitSingleOrNull()
    }
}