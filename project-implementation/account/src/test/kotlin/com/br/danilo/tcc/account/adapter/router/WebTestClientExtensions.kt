package com.br.danilo.tcc.account.adapter.router

import org.springframework.context.ApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient

fun createWebTestClient(applicationContext: ApplicationContext): WebTestClient =
    WebTestClient.bindToApplicationContext(applicationContext).build()

