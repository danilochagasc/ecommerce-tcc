package com.br.danilo.tcc.gateway.adapter.auth

import com.br.danilo.tcc.gateway.adapter.auth.jwt.BearerTokenServerAuthenticationConverter
import com.br.danilo.tcc.gateway.adapter.auth.jwt.JwtReactiveAuthenticationManager
import com.br.danilo.tcc.gateway.domain.auth.TokenValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

/**
 * Configuração de segurança do Gateway.
 * O Gateway apenas valida tokens JWT gerados pelo Account Service e aplica regras de autorização.
 * A geração de tokens é responsabilidade exclusiva do Account Service.
 */
@Configuration
class SecurityConfig(
    private val tokenValidator: TokenValidator,
) {
    @Bean
    fun authenticationManager() = JwtReactiveAuthenticationManager(tokenValidator)

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
                // Public routes - no authentication required
                it.pathMatchers(HttpMethod.GET, *PUBLIC_GET_ROUTES.toTypedArray()).permitAll()
                it.pathMatchers(HttpMethod.POST, *PUBLIC_POST_ROUTES.toTypedArray()).permitAll()
                
                // Admin routes - requires ADMIN role
                it.pathMatchers(HttpMethod.GET, *ADMIN_GET_ROUTES.toTypedArray()).hasRole("ADMIN")
                it.pathMatchers(HttpMethod.POST, *ADMIN_POST_ROUTES.toTypedArray()).hasRole("ADMIN")
                it.pathMatchers(HttpMethod.PUT, *ADMIN_PUT_ROUTES.toTypedArray()).hasRole("ADMIN")
                it.pathMatchers(HttpMethod.DELETE, *ADMIN_DELETE_ROUTES.toTypedArray()).hasRole("ADMIN")
                
                // All other routes require authentication
                it.anyExchange().authenticated()
            }.addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

    companion object {
        val PUBLIC_GET_ROUTES =
            listOf(
                "/actuator/health",
                "/actuator/info",
                "/actuator/prometheus",
                "/product",
                "/product/**",
                "/category",
                "/category/**",
            )

        val PUBLIC_POST_ROUTES =
            listOf(
                "/auth/login",
                "/user/register",
            )

        // Admin routes for managing products, categories, and coupons
        val ADMIN_GET_ROUTES =
            listOf(
                "/user", // List all users
            )

        val ADMIN_POST_ROUTES =
            listOf(
                "/product",
                "/category",
                "/coupon",
            )

        val ADMIN_PUT_ROUTES =
            listOf(
                "/product/**",
                "/category/**",
                "/coupon/**",
            )

        val ADMIN_DELETE_ROUTES =
            listOf(
                "/product/**",
                "/category/**",
                "/coupon/**",
            )
    }
}

