package com.br.danilo.tcc.account.core.domain.user

class UserNotFoundException(
    val userId: UserId,
) : Exception("User with id $userId not found")

class UserNotFoundExceptionByEmail(
    val email: String,
) : Exception("User with email '$email' not found")

class UserAlreadyExistsException(
    val email: String,
) : Exception("User with email '$email' already exists")

class PasswordsDoNotMatchException : Exception("The current password does not match")

class InvalidCredentialsException : Exception("Invalid credentials")
