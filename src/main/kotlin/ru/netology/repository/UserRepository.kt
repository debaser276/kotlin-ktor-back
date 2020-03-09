package ru.netology.repository

import ru.netology.model.UserModel

interface UserRepository {
    suspend fun getAll(): List<UserModel>
    suspend fun getById(id: Int): UserModel?
    suspend fun getByIds(ids: Collection<Int>): List<UserModel>
    suspend fun getByUsername(username: String): UserModel?
    suspend fun add(item: UserModel): UserModel
    suspend fun save(item: UserModel): UserModel
    suspend fun savePushTokenWithUserId(id: Int, token: String)
}