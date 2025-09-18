package com.br.danilo.tcc.checkout.core.domain.coupon

import com.br.danilo.tcc.checkout.core.domain.AggregateId
import org.valiktor.functions.isGreaterThan
import org.valiktor.functions.isLessThanOrEqualTo
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.isNotNull
import org.valiktor.validate

typealias CouponId = AggregateId

data class Coupon(
    val id: CouponId,
    val code: String,
    val discountPercentage: Double,
    val usageLimit: Int?,
){

    init {
        validate()
    }

    private fun validate(){
        validate(this){
            validate(Coupon::code).isNotEmpty()
            validate(Coupon::discountPercentage).isNotNull().isGreaterThan(0.0).isLessThanOrEqualTo(100.0)
            usageLimit?.let { validate(Coupon::usageLimit).isGreaterThan(0) }
        }
    }

    companion object{
        fun create(
            code: String,
            discountPercentage: Double,
            usageLimit: Int?,
        ) = Coupon(
            id = CouponId(),
            code = code,
            discountPercentage = discountPercentage,
            usageLimit = usageLimit,
        )
    }

    fun update(
        code: String,
        discountPercentage: Double,
        usageLimit: Int?,
    ) = this
        .copy(
            code = code,
            discountPercentage = discountPercentage,
            usageLimit = usageLimit,
        )

//    fun getDiscount(): Coupon =
//        usageLimit?.let { if (it > 0) this.copy(usageLimit = it -1) else throw CouponUsageLimitExceededException(this.id) }
//            ?: this

}
