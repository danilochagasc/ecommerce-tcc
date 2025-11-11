package com.br.danilo.tcc.gateway.domain.user

import java.util.UUID

@JvmInline
value class UserId(val value: UUID) {
    constructor(value: String) : this(UUID.fromString(value))

    override fun toString(): String = value.toString()
}

