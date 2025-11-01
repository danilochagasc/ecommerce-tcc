package com.br.danilo.tcc.account.adapter.r2dbc

import com.br.danilo.tcc.account.adapter.r2dbc.queries.AddressSqlQueries.deleteAddress
import com.br.danilo.tcc.account.adapter.r2dbc.queries.AddressSqlQueries.deleteAddressByUserId
import com.br.danilo.tcc.account.adapter.r2dbc.queries.AddressSqlQueries.insertAddress
import com.br.danilo.tcc.account.adapter.r2dbc.queries.AddressSqlQueries.selectAddress
import com.br.danilo.tcc.account.adapter.r2dbc.queries.AddressSqlQueries.updateAddress
import com.br.danilo.tcc.account.adapter.r2dbc.queries.AddressSqlQueries.whereId
import com.br.danilo.tcc.account.adapter.r2dbc.queries.AddressSqlQueries.whereUserId
import com.br.danilo.tcc.account.core.domain.address.Address
import com.br.danilo.tcc.account.core.domain.address.AddressId
import com.br.danilo.tcc.account.core.domain.address.AddressRepository
import com.br.danilo.tcc.account.core.domain.user.UserId
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.toList
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AddressR2dbcRepository(
    private val db: DatabaseClient,
) : AddressRepository {
    override suspend fun findById(id: AddressId): Address? =
        selectAddress()
            .where(whereId(id))
            .run {
                db
                    .sql(this)
                    .bindIfNotNull("id", id.toUUID())
                    .map { row, _ ->
                        row.toAddress()
                    }.awaitOneOrNull()
            }

    override suspend fun findAllByUserId(userId: UserId): List<Address> =
        selectAddress()
            .where(whereUserId(userId))
            .run {
                db
                    .sql(this)
                    .bind("userId", userId.toUUID())
                    .map { row, _ -> row.toAddress() }
                    .flow()
                    .toList()
            }

    override suspend fun create(address: Address) {
        db
            .sql(insertAddress())
            .bind("id", address.id.toUUID())
            .bind("userId", address.userId.toUUID())
            .bind("street", address.street)
            .bind("number", address.number)
            .bindOrNull("complement", address.complement)
            .bind("neighborhood", address.neighborhood)
            .bind("city", address.city)
            .bind("state", address.state)
            .bind("zipCode", address.zipCode)
            .await()
    }

    override suspend fun update(address: Address) {
        db
            .sql(updateAddress())
            .bind("id", address.id.toUUID())
            .bind("street", address.street)
            .bind("number", address.number)
            .bindOrNull("complement", address.complement)
            .bind("neighborhood", address.neighborhood)
            .bind("city", address.city)
            .bind("state", address.state)
            .bind("zipCode", address.zipCode)
            .await()
    }

    override suspend fun delete(id: AddressId) {
        db
            .sql(deleteAddress())
            .bind("id", id.toUUID())
            .await()
    }

    override suspend fun deleteAllByUserId(userId: UserId) {
        db
            .sql(deleteAddressByUserId())
            .bind("userId", userId.toUUID())
            .await()
    }

    private fun Row.toAddress() =
        Address(
            id = AddressId(this.get<UUID>("id")),
            userId = UserId(this.get<UUID>("user_id")),
            street = this.get<String>("street"),
            number = this.get<String>("number"),
            complement = this.getOrNull<String>("complement"),
            neighborhood = this.get<String>("neighborhood"),
            city = this.get<String>("city"),
            state = this.get<String>("state"),
            zipCode = this.get<String>("zip_code"),
        )
}
