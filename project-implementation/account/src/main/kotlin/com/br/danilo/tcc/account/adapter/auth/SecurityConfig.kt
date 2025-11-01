package com.br.danilo.tcc.account.adapter.auth

import com.br.danilo.tcc.account.adapter.auth.jwt.BearerTokenServerAuthenticationConverter
import com.br.danilo.tcc.account.adapter.auth.jwt.JwtReactiveAuthenticationManager
import com.br.danilo.tcc.account.core.domain.auth.TokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@Configuration
class SecurityConfig(
    private val jwtProvider: TokenProvider,
) {
    @Bean
    fun authenticationManager() = JwtReactiveAuthenticationManager(jwtProvider)

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val authFilter = AuthenticationWebFilter(authenticationManager())
        authFilter.setServerAuthenticationConverter(BearerTokenServerAuthenticationConverter())

        authFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())

        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange {
                it.pathMatchers(HttpMethod.GET, *PUBLIC_GET_ROUTES.toTypedArray()).permitAll()
                it.pathMatchers(HttpMethod.POST, *PUBLIC_POST_ROUTES.toTypedArray()).permitAll()
                it.pathMatchers(HttpMethod.GET, *ADMIN_GET_ROUTES.toTypedArray()).hasRole("ADMIN")
                it.anyExchange().authenticated()
            }.addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

    companion object {
        val PUBLIC_GET_ROUTES =
            listOf(
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/actuator/health",
                "/actuator/info",
                "/actuator/prometheus",
            )

        val PUBLIC_POST_ROUTES =
            listOf(
                "/auth/login",
                "/user/register",
            )

        val ADMIN_GET_ROUTES =
            listOf(
                "/user",
            )
    }
}
