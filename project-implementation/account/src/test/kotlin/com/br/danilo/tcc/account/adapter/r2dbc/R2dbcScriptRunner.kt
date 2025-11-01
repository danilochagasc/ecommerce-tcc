package com.br.danilo.tcc.account.adapter.r2dbc

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

        val sql = R2dbcScriptRunner::class.java.classLoader.getResource(path)
            ?.readText(StandardCharsets.UTF_8)
            ?: throw IllegalArgumentException("Script $path not found")

        // Normalize SQL: remove extra whitespace and newlines, then split by semicolon
        // PostgreSQL R2DBC driver doesn't support multiple statements in a single call
        val normalizedSql = sql
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .replace(Regex("\\s+"), " ")
            .trim()

        // Split by semicolon and execute each statement separately
        normalizedSql.split(";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { statement ->
                db.sql(statement).await()
            }
    }
}

