package com.br.danilo.tcc.account.core.application.address

import com.br.danilo.tcc.account.core.application.address.command.CreateAddressCommand
import com.br.danilo.tcc.account.core.application.address.command.UpdateAddressCommand
import com.br.danilo.tcc.account.core.domain.address.AddressId
import com.br.danilo.tcc.account.core.domain.user.UserId

object AddressServiceTestFixture {

    fun createAddressCommand(
        userId: UserId = UserId(),
        street: String = "Main Street",
        number: String = "123",
        complement: String? = null,
        neighborhood: String = "Downtown",
        city: String = "São Paulo",
        state: String = "SP",
        zipCode: String = "01234567",
    ) = CreateAddressCommand(
        userId = userId,
        street = street,
        number = number,
        complement = complement,
        neighborhood = neighborhood,
        city = city,
        state = state,
        zipCode = zipCode,
    )

    fun updateAddressCommand(
        id: AddressId = AddressId(),
        street: String = "Updated Street",
        number: String = "456",
        complement: String? = "Apt 101",
        neighborhood: String = "Uptown",
        city: String = "Rio de Janeiro",
        state: String = "RJ",
        zipCode: String = "20000000",
    ) = UpdateAddressCommand(
        id = id,
        street = street,
        number = number,
        complement = complement,
        neighborhood = neighborhood,
        city = city,
        state = state,
        zipCode = zipCode,
    )
}

