package com.br.danilo.tcc.order.adapter.r2dbc.queries

import com.br.danilo.tcc.order.core.domain.AggregateId
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
