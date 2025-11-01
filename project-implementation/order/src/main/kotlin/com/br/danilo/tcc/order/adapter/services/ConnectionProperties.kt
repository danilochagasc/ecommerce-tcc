package com.br.danilo.tcc.order.adapter.services

import java.time.Duration

interface ConnectionProperties {
    val baseUrl: String
    val connTimeout: Duration
    val connReadTimeout: Duration
}
