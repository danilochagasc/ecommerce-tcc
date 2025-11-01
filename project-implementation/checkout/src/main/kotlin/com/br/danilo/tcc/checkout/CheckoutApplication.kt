package com.br.danilo.tcc.checkout

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class CheckoutApplication

fun main(args: Array<String>) {
    runApplication<CheckoutApplication>(*args)
}
