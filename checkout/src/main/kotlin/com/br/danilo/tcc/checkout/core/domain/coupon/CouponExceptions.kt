package com.br.danilo.tcc.checkout.core.domain.coupon

class CouponNotFoundException(
    val couponId: CouponId,
) : Exception("Coupon with id $couponId not found")

class CouponAlreadyExistsException(
    val code: String,
) : Exception("Coupon with code '$code' already exists")

class CouponUsageLimitExceededException(
    val couponId: CouponId,
) : Exception("Coupon with id $couponId request limit exceeded")