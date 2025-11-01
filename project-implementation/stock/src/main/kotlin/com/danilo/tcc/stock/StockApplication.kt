package com.danilo.tcc.stock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class StockApplication

fun main(args: Array<String>) {
    runApplication<StockApplication>(*args)
}
