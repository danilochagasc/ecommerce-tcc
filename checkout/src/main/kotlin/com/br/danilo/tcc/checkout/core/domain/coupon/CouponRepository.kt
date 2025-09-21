package com.br.danilo.tcc.checkout.core.domain.coupon

interface CouponRepository {
    suspend fun findAll(): List<Coupon>

    suspend fun findByCode(code: String): Coupon?

    suspend fun createOrUpdate(coupon: Coupon): Coupon

    suspend fun delete(code: String)
}
