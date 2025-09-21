package com.br.danilo.tcc.checkout.core.application.coupon

import com.br.danilo.tcc.checkout.core.application.coupon.command.CreateCouponCommand
import com.br.danilo.tcc.checkout.core.application.coupon.command.UpdateCouponCommand
import com.br.danilo.tcc.checkout.core.domain.coupon.Coupon
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponAlreadyExistsException
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponNotFoundException
import com.br.danilo.tcc.checkout.core.domain.coupon.CouponRepository
import org.springframework.stereotype.Service

@Service
class CouponService(
    private val repository: CouponRepository,
) {
    suspend fun findAll(): List<Coupon> = repository.findAll()

    suspend fun findByCode(code: String): Coupon = returnCouponIfExists(code)

    suspend fun create(command: CreateCouponCommand): String {
        repository.findByCode(command.code)?.let {
            throw CouponAlreadyExistsException(command.code)
        }
        val coupon =
            Coupon.create(
                code = command.code,
                discountType = command.discountType,
                value = command.value,
                expiresAt = command.expiresAt,
            )

        repository.createOrUpdate(coupon)

        return coupon.code
    }

    suspend fun update(command: UpdateCouponCommand){
        val existingCoupon = returnCouponIfExists(command.code)
        val updatedCoupon = existingCoupon.update(
            discountType = command.discountType,
            value = command.value,
            expiresAt = command.expiresAt,
        )
        repository.createOrUpdate(updatedCoupon)
    }

    suspend fun delete(code: String) {
        returnCouponIfExists(code)
        repository.delete(code)
    }

    private suspend fun returnCouponIfExists(code: String) = repository.findByCode(code) ?: throw CouponNotFoundException(code)
}
