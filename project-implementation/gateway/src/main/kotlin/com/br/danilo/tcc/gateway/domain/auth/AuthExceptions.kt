package com.br.danilo.tcc.gateway.domain.auth

class InvalidTokenException(message: String = "Invalid token") : RuntimeException(message)

class ExpiredTokenException(message: String = "Token has expired") : RuntimeException(message)

