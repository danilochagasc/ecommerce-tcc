package com.br.danilo.tcc.account.adapter.r2dbc.queries

import kotlin.let

abstract class DefaultSqlQueries {
    fun limit(limit: Int?) =
        limit?.let {
            """
           LIMIT :limit
        """
        }

    fun offset(offset: Int?) =
        offset?.let {
            """
           OFFSET :offset
        """
        }

    fun sortingBy(
        field: String,
        asc: Boolean = true,
    ) = """
        ORDER BY $field ${if (asc) "ASC" else "DESC"}
        """
}
