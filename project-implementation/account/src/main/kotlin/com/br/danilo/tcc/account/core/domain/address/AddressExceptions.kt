package com.br.danilo.tcc.account.core.domain.address

import com.br.danilo.tcc.account.core.domain.user.UserId

class AddressNotFoundException(
    val addressId: AddressId,
) : Exception("Address with id $addressId not found")

class AddressNotBelongsToUserException(
    val addressId: AddressId,
    val userId: UserId,
) : Exception("Address with id $addressId does not belong to user with id $userId")

class AddressAlreadyExistsException(
    val userId: String,
    val street: String,
    val number: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
) : Exception("Address for user '$userId' at '$street, $number, $neighborhood, $city, $state, $zipCode' already exists")
