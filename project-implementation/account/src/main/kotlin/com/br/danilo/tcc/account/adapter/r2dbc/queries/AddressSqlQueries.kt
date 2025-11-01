package com.br.danilo.tcc.account.adapter.r2dbc.queries

import com.br.danilo.tcc.account.core.domain.address.AddressId
import com.br.danilo.tcc.account.core.domain.user.UserId

object AddressSqlQueries {
    fun selectAddress() =
        """
        SELECT * 
          FROM address
         WHERE 1 = 1
        """

    fun whereId(id: AddressId?) =
        id?.let {
            """
           AND id = :id
        """
        }

    fun whereUserId(userId: UserId?) =
        userId?.let {
            """
           AND user_id = :userId
        """
        }

    fun insertAddress() =
        """
            INSERT INTO address(id, user_id, street, number, complement, neighborhood, city, state, zip_code)
            VALUES (:id, :userId, :street, :number, :complement, :neighborhood, :city, :state, :zipCode)
        """

    fun updateAddress() =
        """
            UPDATE address
               SET street = :street,
                   number = :number,
                   complement = :complement,
                   neighborhood = :neighborhood,
                   city = :city,
                   state = :state,
                   zip_code = :zipCode
             WHERE id = :id
        """

    fun deleteAddress() =
        """
            DELETE FROM address
             WHERE id = :id
        """

    fun deleteAddressByUserId() =
        """
            DELETE FROM address
             WHERE user_id = :userId
        """
}
