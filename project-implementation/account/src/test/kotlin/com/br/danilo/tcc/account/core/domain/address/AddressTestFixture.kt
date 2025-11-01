package com.br.danilo.tcc.account.core.domain.address

import com.br.danilo.tcc.account.core.domain.user.UserId

object AddressTestFixture {

    fun address(
        userId: UserId = UserId(),
        street: String = "Main Street",
        number: String = "123",
        complement: String? = null,
        neighborhood: String = "Downtown",
        city: String = "São Paulo",
        state: String = "SP",
        zipCode: String = "01234567",
    ) = Address.create(
        userId = userId,
        street = street,
        number = number,
        complement = complement,
        neighborhood = neighborhood,
        city = city,
        state = state,
        zipCode = zipCode,
    )
}

