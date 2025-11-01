package com.br.danilo.tcc.account.adapter.auth

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@TestConfiguration
class TestSecurityConfig {

    @Bean
    @Primary
    fun testSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange {
                it.anyExchange().permitAll()
            }
            // Don't configure securityContextRepository - let Spring Security use default
            // SecurityMockServerConfigurers.mockAuthentication injects SecurityContext
            // into Reactor Context which ReactiveSecurityContextHolder can read
            .build()
    }
}

