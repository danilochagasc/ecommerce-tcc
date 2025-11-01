package com.br.danilo.tcc.account.core.domain.auth

class InvalidTokenException : Exception("Invalid or expired token")

class TokenGenerationException : Exception("Error generating token")
