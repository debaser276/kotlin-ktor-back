package ru.netology.service

import io.ktor.features.NotFoundException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.security.crypto.password.PasswordEncoder
import ru.netology.dto.AuthenticationRequestDto
import ru.netology.dto.AuthenticationResponseDto
import ru.netology.dto.UserResponseDto
import ru.netology.exception.InvalidPasswordException
import ru.netology.exception.UserNotFoundException
import ru.netology.model.UserModel
import ru.netology.repository.UserRepository
import java.nio.channels.AlreadyBoundException

class UserService(
    private val repo: UserRepository,
    private val tokenService: JWTTokenService,
    private val passwordEncoder: PasswordEncoder
) {
    private val mutex = Mutex()

    suspend fun getModelById(id: Int): UserModel? = repo.getById(id)

    suspend fun getById(id: Int): UserResponseDto {
        val model = repo.getById(id) ?: throw UserNotFoundException()
        return UserResponseDto.fromModel(model)
    }

    suspend fun getByUsername(username: String): UserModel? = repo.getByUsername(username)

    suspend fun registrate(input: AuthenticationRequestDto): AuthenticationResponseDto {
        mutex.withLock {
            val model =
                repo.add(UserModel(username = input.username, password = passwordEncoder.encode(input.password)))
            val token = tokenService.generate(model.id)
            return AuthenticationResponseDto(model.id, token)
        }
    }

    suspend fun authenticate(input: AuthenticationRequestDto): AuthenticationResponseDto {
        val model = repo.getByUsername(input.username) ?: throw UserNotFoundException()
        if (!passwordEncoder.matches(input.password, model.password)) {
            throw InvalidPasswordException()
        }
        val token = tokenService.generate(model.id)
        return AuthenticationResponseDto(model.id, token)
    }
}