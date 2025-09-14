package com.danilo.tcc.stock.adapter.r2dbc.queries

import com.danilo.tcc.stock.core.domain.AggregateId
import com.danilo.tcc.stock.core.domain.category.CategoryId
import com.danilo.tcc.stock.core.domain.product.ProductId
import java.time.OffsetDateTime
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.text.replace

fun String.quoteLiteral() = "'${this.replace("'", "''")}'"

fun AggregateId.quoteLiteral() = "'${this.toUUID()}'"

fun Int.quoteLiteral() = "'$this'"

fun OffsetDateTime.quoteLiteral() = "'$this'"

fun Collection<String>.toStringArray(): String =
    if (this.isNotEmpty()) {
        this.joinToString(separator = ",") { it.quoteLiteral() }
    } else {
        "''"
    }

fun Collection<String>.toStringHstore(): String =
    if (this.isNotEmpty()) {
        this.joinToString(separator = ",")
    } else {
        "''"
    }.quoteLiteral()

fun Map<String, String>.toStringHstore(): String =
    this
        .map {
            "\"${it.key}\" => \"${it.value}\""
        }.toStringHstore()
