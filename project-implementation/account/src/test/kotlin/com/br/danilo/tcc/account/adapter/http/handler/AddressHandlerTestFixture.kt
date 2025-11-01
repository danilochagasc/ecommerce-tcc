package com.br.danilo.tcc.account.adapter.http.handler

import com.br.danilo.tcc.account.core.application.address.query.AddressQuery
import com.br.danilo.tcc.account.core.domain.address.AddressId
import com.br.danilo.tcc.account.core.domain.user.UserId

object AddressHandlerTestFixture {

    fun addressQuery() =
        AddressQuery(
            id = AddressId(),
            street = "Main Street",
            number = "123",
            complement = null,
            neighborhood = "Downtown",
            city = "São Paulo",
            state = "SP",
            zipCode = "01234567",
        )

    fun anotherAddressQuery() =
        AddressQuery(
            id = AddressId(),
            street = "Second Street",
            number = "456",
            complement = "Apt 101",
            neighborhood = "Uptown",
            city = "Rio de Janeiro",
            state = "RJ",
            zipCode = "20000000",
        )
}

