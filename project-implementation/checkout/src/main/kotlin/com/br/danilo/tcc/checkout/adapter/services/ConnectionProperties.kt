package com.br.danilo.tcc.checkout.adapter.services

import java.time.Duration

interface ConnectionProperties {
    val baseUrl: String
    val connTimeout: Duration
    val connReadTimeout: Duration
}
