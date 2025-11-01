package com.danilo.tcc.stock.adapter.r2dbc.queries

import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.product.ProductId

object ProductSqlQueries {
    fun selectProduct() =
        """
        SELECT * 
          FROM product
         WHERE 1 = 1
        """

    fun whereId(id: ProductId?) =
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

    fun whereCategoryId(categoryId: CategoryId?) =
        categoryId?.let {
            """
           AND category_id = :categoryId
        """
        }

    fun insertProduct() =
        """
            INSERT INTO product(id, name, description, image_url, price, quantity, category_id)
            VALUES (:id, :name, :description, :image_url, :price, :quantity, :category_id)
        """

    fun updateProduct() =
        """
            UPDATE product
               SET name = :name,
                   description = :description,
                   image_url = :image_url,
                   price = :price,
                   quantity = :quantity,
                   category_id = :category_id
             WHERE id = :id
        """

    fun deleteProduct() =
        """
            DELETE FROM product
             WHERE id = :id
        """
}
