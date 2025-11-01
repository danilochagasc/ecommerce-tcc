package com.br.danilo.tcc.account.core.application.address

import com.br.danilo.tcc.account.core.application.address.command.CreateAddressCommand
import com.br.danilo.tcc.account.core.application.address.command.UpdateAddressCommand
import com.br.danilo.tcc.account.core.application.address.query.toQuery
import com.br.danilo.tcc.account.core.domain.address.Address
import com.br.danilo.tcc.account.core.domain.address.AddressId
import com.br.danilo.tcc.account.core.domain.address.AddressNotFoundException
import com.br.danilo.tcc.account.core.domain.address.AddressRepository
import com.br.danilo.tcc.account.core.domain.user.UserId
import org.springframework.stereotype.Service

@Service
class AddressService(
    private val repository: AddressRepository,
) {
    suspend fun findById(id: AddressId) = repository.findById(id)?.toQuery()

    suspend fun findAllByUserId(userId: UserId) = repository.findAllByUserId(userId).map { it.toQuery() }

    suspend fun create(command: CreateAddressCommand): AddressId {
        with(command) {
            val address =
                Address.create(
                    userId = userId,
                    street = street,
                    number = number,
                    complement = complement,
                    neighborhood = neighborhood,
                    city = city,
                    state = state,
                    zipCode = zipCode,
                )
            repository.create(address)
            return address.id
        }
    }

    suspend fun update(command: UpdateAddressCommand) {
        with(command) {
            val address = returnAddressIfExists(id)
            val updatedAddress =
                address.update(
                    street = street,
                    number = number,
                    complement = complement,
                    neighborhood = neighborhood,
                    city = city,
                    state = state,
                    zipCode = zipCode,
                )
            repository.update(updatedAddress)
        }
    }

    suspend fun delete(id: AddressId) {
        val address = returnAddressIfExists(id)
        repository.delete(address.id)
    }

    suspend fun deleteAllByUserId(userId: UserId) {
        repository.deleteAllByUserId(userId)
    }

    private suspend fun returnAddressIfExists(id: AddressId) = repository.findById(id) ?: throw AddressNotFoundException(id)
}
