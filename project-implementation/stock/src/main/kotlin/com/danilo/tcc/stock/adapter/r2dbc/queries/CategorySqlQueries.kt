package com.danilo.tcc.stock.adapter.r2dbc.queries

import com.danilo.tcc.stock.core.domain.category.CategoryId

object CategorySqlQueries {
    fun selectCategory() =
        """
        SELECT * 
          FROM category
         WHERE 1 = 1
        """

    fun whereId(id: CategoryId?) =
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

    fun insertCategory() =
        """
            INSERT INTO category(id, name)
            VALUES (:id, :name)
        """

    fun updateCategory() =
        """
            UPDATE category
               SET name = :name
             WHERE id = :id
        """

    fun deleteCategory() =
        """
            DELETE FROM category
             WHERE id = :id
        """
}
