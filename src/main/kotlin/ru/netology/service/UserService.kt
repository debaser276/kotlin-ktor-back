package ru.netology.service

import ru.netology.repository.DatabaseFactory.dbQuery
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.security.crypto.password.PasswordEncoder
import ru.netology.dto.AuthenticationRequestDto
import ru.netology.dto.AuthenticationResponseDto
import ru.netology.exception.*
import ru.netology.model.NewUser
import ru.netology.model.UserModel
import ru.netology.model.Users

class UserService(
    private val tokenService: JWTTokenService,
    private val passwordEncoder: PasswordEncoder
) {
    private val pushTokenWithUserIdMap = mutableMapOf<Int, String>()
    private val mutex = Mutex()

    suspend fun insertUser(user: NewUser) = dbQuery {
        Users.insert {
            it[username] = user.username
            it[password] = user.password
        }
    }

    suspend fun getModelById(id: Int): UserModel? =
        dbQuery {
            Users.select{ Users.id eq id }.mapNotNull { toUserModel(it) }.singleOrNull()
        }

    suspend fun getByUsername(username: String): UserModel? =
        dbQuery {
            Users.select{ Users.username eq username }.mapNotNull { toUserModel(it) }.singleOrNull()
        }


    suspend fun register(input: AuthenticationRequestDto): AuthenticationResponseDto {
        mutex.withLock {
            val user = NewUser(
                username = input.username,
                password = passwordEncoder.encode(input.password)
            )
            insertUser(user)
            val id = getByUsername(user.username)?.id ?: throw LoginAlreadyExistsException()
            val token = tokenService.generate(id)
            return AuthenticationResponseDto(id, token)
        }
    }

    suspend fun authenticate(input: AuthenticationRequestDto): AuthenticationResponseDto {
        val user = getByUsername(input.username) ?: throw UserNotFoundException()
        if (!passwordEncoder.matches(input.password, user.password)) {
            throw InvalidPasswordException()
        }
        val token = tokenService.generate(user.id)
        return AuthenticationResponseDto(user.id, token)
    }

    fun getPushTokenById(id: Int): String =
        pushTokenWithUserIdMap.getOrElse(id, throw PushTokenNotFoundException())

    fun savePushTokenWithUserId(id: Int, token: String) {
        pushTokenWithUserIdMap[id] = token
    }

    private fun toUserModel(row: ResultRow): UserModel =
        UserModel(
            id = row[Users.id],
            username = row[Users.username],
            password = row[Users.password]
        )
}