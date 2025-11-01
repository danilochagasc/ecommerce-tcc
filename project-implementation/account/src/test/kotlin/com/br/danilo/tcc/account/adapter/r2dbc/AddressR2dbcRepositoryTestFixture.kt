package com.br.danilo.tcc.account.adapter.r2dbc

import com.br.danilo.tcc.account.core.domain.address.Address
import com.br.danilo.tcc.account.core.domain.address.AddressId
import com.br.danilo.tcc.account.core.domain.user.UserId
import java.util.UUID

object AddressR2dbcRepositoryTestFixture {

    private val userId = UserId(UUID.fromString("8b1c7a4d-6e58-465e-bc34-b43cf7649b87"))
    private val addressId = AddressId(UUID.fromString("17f8b4b5-c55a-4f6d-8427-3f80eb75b801"))
    private val anotherAddressId = AddressId(UUID.fromString("a7d9c3e2-4b8f-4a1d-9c5e-3f2a1b4c5d6e"))

    fun userId() = userId
    fun addressId() = addressId
    fun anotherAddressId() = anotherAddressId

    fun address() =
        Address(
            id = addressId,
            userId = userId,
            street = "Main Street",
            number = "123",
            complement = null,
            neighborhood = "Downtown",
            city = "São Paulo",
            state = "SP",
            zipCode = "01234567",
        )

    fun anotherAddress() =
        Address(
            id = anotherAddressId,
            userId = userId,
            street = "Second Street",
            number = "456",
            complement = "Apt 101",
            neighborhood = "Uptown",
            city = "Rio de Janeiro",
            state = "RJ",
            zipCode = "20000000",
        )
}

