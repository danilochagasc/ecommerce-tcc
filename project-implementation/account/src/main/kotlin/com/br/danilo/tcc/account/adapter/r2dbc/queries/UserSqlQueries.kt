package com.br.danilo.tcc.account.adapter.r2dbc.queries

import com.br.danilo.tcc.account.core.domain.user.UserId

object UserSqlQueries {
    fun selectUser() =
        """
        SELECT *
            FROM "user"
         WHERE 1 = 1
        """

    fun whereId(id: UserId?) =
        id?.let {
            """
           AND id = :id
        """
        }

    fun whereName(name: String?) =
        name?.let {
            """
           AND name = :name
        """
        }

    fun whereEmail(email: String?) =
        email?.let {
            """
           AND email = :email
        """
        }

    fun insertUser() =
        """
            INSERT INTO "user"(id, name, last_name, cpf, email, password, phone, birth_date, role)
            VALUES (:id, :name, :lastName, :cpf, :email, :password, :phone, :birthDate, :role)
        """

    fun updateUser() =
        """
            UPDATE "user"
               SET name = :name,
                   last_name = :lastName,
                   cpf = :cpf,
                   email = :email,
                   password = :password,
                   phone = :phone,
                   birth_date = :birthDate,
                   role = :role
             WHERE id = :id
        """

    fun deleteUser() =
        """
            DELETE FROM "user"
             WHERE id = :id
        """
}
