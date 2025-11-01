package com.br.danilo.tcc.account.core.domain.address

import com.br.danilo.tcc.account.core.domain.user.UserId

interface AddressRepository {
    suspend fun findById(id: AddressId): Address?

    suspend fun findAllByUserId(userId: UserId): List<Address>

    suspend fun create(address: Address)

    suspend fun update(address: Address)

    suspend fun delete(id: AddressId)

    suspend fun deleteAllByUserId(userId: UserId)
}
