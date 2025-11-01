package com.br.danilo.tcc.account.core.application.user

import com.br.danilo.tcc.account.core.application.address.AddressService
import com.br.danilo.tcc.account.core.application.user.command.ChangePasswordCommand
import com.br.danilo.tcc.account.core.application.user.command.CreateUserCommand
import com.br.danilo.tcc.account.core.application.user.command.UpdateUserCommand
import com.br.danilo.tcc.account.core.application.user.query.toPrivateQuery
import com.br.danilo.tcc.account.core.application.user.query.toPublicQuery
import com.br.danilo.tcc.account.core.domain.auth.PasswordHasher
import com.br.danilo.tcc.account.core.domain.user.PasswordsDoNotMatchException
import com.br.danilo.tcc.account.core.domain.user.User
import com.br.danilo.tcc.account.core.domain.user.UserAlreadyExistsException
import com.br.danilo.tcc.account.core.domain.user.UserId
import com.br.danilo.tcc.account.core.domain.user.UserNotFoundException
import com.br.danilo.tcc.account.core.domain.user.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
    private val addressService: AddressService,
    private val passwordHasher: PasswordHasher,
) {
    suspend fun findAll() = repository.findAll().map { it.toPrivateQuery() }

    suspend fun findById(id: UserId) = returnUserIfExists(id).toPublicQuery()

    suspend fun create(command: CreateUserCommand) {
        repository.findByEmail(command.email)?.let {
            throw UserAlreadyExistsException(command.email)
        }

        with(command) {
            val user =
                User.create(
                    name = name,
                    lastName = lastName,
                    cpf = cpf,
                    email = email,
                    password = passwordHasher.hash(password),
                    phone = phone,
                    birth = birth,
                    role = enumValueOf(role),
                )
            repository.create(user)
        }
    }

    suspend fun update(command: UpdateUserCommand) {
        val user = returnUserIfExists(command.id)
        repository.findByEmail(command.email)?.let {
            if (it.id != command.id) throw UserAlreadyExistsException(command.email)
        }

        with(command) {
            val updatedUser =
                user.update(
                    name = name,
                    lastName = lastName,
                    cpf = cpf,
                    email = email,
                    password = user.password,
                    phone = phone,
                    birth = birth,
                )
            repository.update(updatedUser)
        }
    }

    suspend fun changePassword(command: ChangePasswordCommand) {
        val user = returnUserIfExists(command.id)

        with(command) {
            passwordHasher.verify(command.currentPassword, user.password) ||
                throw PasswordsDoNotMatchException()

            val updatedUser =
                user.changePassword(
                    newPassword = passwordHasher.hash(newPassword),
                )
            repository.update(updatedUser)
        }
    }

    suspend fun delete(id: UserId) {
        repository.findById(id) ?: throw UserNotFoundException(id)
        addressService.deleteAllByUserId(id)
        repository.delete(id)
    }

    suspend fun returnUserIfExists(id: UserId) = repository.findById(id) ?: throw UserNotFoundException(id)
}
