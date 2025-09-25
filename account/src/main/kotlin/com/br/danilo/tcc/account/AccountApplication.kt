package com.br.danilo.tcc.account

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class AccountApplication

fun main(args: Array<String>) {
    runApplication<AccountApplication>(*args)
}
