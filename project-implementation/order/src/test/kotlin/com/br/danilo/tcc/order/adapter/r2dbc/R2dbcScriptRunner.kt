package com.br.danilo.tcc.order.adapter.r2dbc

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class R2dbcScriptRunner(
    private val db: DatabaseClient,
) {
    suspend fun run(script: String) {
        val path = "db/scripts/$script"

        val sql =
            R2dbcScriptRunner::class.java.classLoader
                .getResource(path)
                ?.readText(StandardCharsets.UTF_8)
                ?: throw IllegalArgumentException("Script $path not found")

        val normalizedSql =
            sql
                .lines()
                .map { it.trim() }
                .filter { it.isNotBlank() && !it.startsWith("--") }
                .joinToString(" ")
                .replace(Regex("\\s+"), " ")
                .trim()

        // Split by semicolon and execute each statement separately
        normalizedSql
            .split(";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { statement ->
                db.sql(statement).await()
            }
    }
}

