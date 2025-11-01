package com.danilo.tcc.stock.core.domain.category

import com.danilo.tcc.stock.core.domain.AggregateId
import org.valiktor.functions.isNotBlank
import org.valiktor.validate
import java.util.UUID

typealias CategoryId = AggregateId

data class Category(
    val id: CategoryId,
    val name: String,
) {

    init{
        validate()
    }

    private fun validate() {
        validate(this) {
            validate(Category::name).isNotBlank()
        }
    }

    companion object {
        fun create(name: String) =
            Category(
                id = CategoryId(UUID.randomUUID()),
                name = name,
            )
    }

    fun update(name: String): Category =
        this
            .copy(
                name = name,
            )
}
