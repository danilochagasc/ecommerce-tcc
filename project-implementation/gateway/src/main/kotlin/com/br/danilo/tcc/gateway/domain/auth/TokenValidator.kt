package com.br.danilo.tcc.gateway.domain.auth

/**
 * Interface para validação e decodificação de tokens JWT.
 * O Gateway apenas valida tokens gerados pelo Account Service.
 * A geração de tokens é responsabilidade exclusiva do Account Service.
 */
interface TokenValidator {
    /**
     * Valida se o token é válido (assinatura correta e não expirado)
     */
    fun validateToken(token: String): Boolean

    /**
     * Extrai os claims (dados) do token JWT
     */
    fun getClaims(token: String): TokenClaims
}

