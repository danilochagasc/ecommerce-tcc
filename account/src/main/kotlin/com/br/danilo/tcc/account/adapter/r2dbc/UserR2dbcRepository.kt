package com.br.danilo.tcc.account.adapter.r2dbc

import com.br.danilo.tcc.account.adapter.r2dbc.queries.UserSqlQueries.deleteUser
import com.br.danilo.tcc.account.adapter.r2dbc.queries.UserSqlQueries.insertUser
import com.br.danilo.tcc.account.adapter.r2dbc.queries.UserSqlQueries.selectUser
import com.br.danilo.tcc.account.adapter.r2dbc.queries.UserSqlQueries.updateUser
import com.br.danilo.tcc.account.adapter.r2dbc.queries.UserSqlQueries.whereEmail
import com.br.danilo.tcc.account.adapter.r2dbc.queries.UserSqlQueries.whereId
import com.br.danilo.tcc.account.core.domain.user.User
import com.br.danilo.tcc.account.core.domain.user.UserId
import com.br.danilo.tcc.account.core.domain.user.UserRepository
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.toList
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
class UserR2dbcRepository(
    private val db: DatabaseClient,
) : UserRepository {
    override suspend fun findAll(): List<User> =
        selectUser()
            .run {
                db
                    .sql(this)
                    .map { row, _ -> row.toUser() }
                    .flow()
                    .toList()
            }

    override suspend fun findById(id: UserId): User? =
        selectUser()
            .where(whereId(id))
            .run {
                db
                    .sql(this)
                    .bindIfNotNull("id", id.toUUID())
                    .map { row, _ -> row.toUser() }
                    .awaitOneOrNull()
            }

    override suspend fun findByEmail(email: String): User? =
        selectUser()
            .where(whereEmail(email))
            .run {
                db
                    .sql(this)
                    .bindIfNotNull("email", email)
                    .map { row, _ -> row.toUser() }
                    .awaitOneOrNull()
            }

    override suspend fun create(user: User) {
        db
            .sql(insertUser())
            .bind("id", user.id.toUUID())
            .bind("name", user.name)
            .bind("lastName", user.lastName)
            .bind("cpf", user.cpf)
            .bind("email", user.email)
            .bind("password", user.password)
            .bind("phone", user.phone)
            .bind("birthDate", user.birth)
            .bind("role", user.role.name)
            .await()
    }

    override suspend fun update(user: User) {
        db
            .sql(updateUser())
            .bind("id", user.id.toUUID())
            .bind("name", user.name)
            .bind("lastName", user.lastName)
            .bind("cpf", user.cpf)
            .bind("email", user.email)
            .bind("password", user.password)
            .bind("phone", user.phone)
            .bind("birthDate", user.birth)
            .bind("role", user.role.name)
            .await()
    }

    override suspend fun delete(id: UserId) {
        db
            .sql(deleteUser())
            .bind("id", id.toUUID())
            .await()
    }

    private fun Row.toUser() =
        User(
            id = UserId(this.get<UUID>("id")),
            name = this.get<String>("name"),
            lastName = this.get<String>("last_name"),
            cpf = this.get<String>("cpf"),
            email = this.get<String>("email"),
            password = this.get<String>("password"),
            phone = this.get<String>("phone"),
            birth = this.get<LocalDate>("birth_date"),
            addresses = emptySet(),
            role = enumValueOf(this.get<String>("role")),
        )
}
