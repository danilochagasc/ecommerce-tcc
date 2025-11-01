package com.br.danilo.tcc.account.adapter.r2dbc

import io.r2dbc.spi.Row
import org.springframework.r2dbc.core.DatabaseClient
import kotlin.jvm.java
import kotlin.let
import kotlin.text.trimEnd

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T> Row.get(identifier: String): T = this.get(identifier, T::class.java)!!

inline fun <reified T> Row.getOrNull(identifier: String): T? = this.get(identifier, T::class.java)

fun String.where(sql: String?) = sql?.let { "${this.trimEnd()} $sql" } ?: this

fun String.orderBy(sql: String?) = sql?.let { "${this.trimEnd()} $sql" } ?: this

inline fun <reified T> DatabaseClient.GenericExecuteSpec.bindIfNotNull(
    name: String,
    value: T?,
) = value?.let { this.bind(name, it as Any) } ?: this

inline fun <reified T> DatabaseClient.GenericExecuteSpec.bindOrNull(
    name: String,
    value: T?,
) = value?.let { this.bind(name, it as Any) } ?: this.bindNull(name, T::class.java)
