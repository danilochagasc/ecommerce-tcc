package com.br.danilo.tcc.checkout.core.domain.coupon

class CouponNotFoundException(
    val code: String,
) : Exception("Coupon with code $code not found")

class CouponAlreadyExistsException(
    val code: String,
) : Exception("Coupon with code '$code' already exists")

class CouponExpiredException(
    val code: String,
) : Exception("Coupon with code $code is expired")
