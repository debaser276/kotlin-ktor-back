package ru.netology.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import ru.netology.model.Posts
import ru.netology.model.UserModel
import ru.netology.model.Users
import ru.netology.repository.DatabaseFactory.dbQuery

interface UserRepository {
    suspend fun getAll(): List<UserModel>
    suspend fun getById(id: Int): UserModel?
    suspend fun getByIds(ids: Collection<Int>): List<UserModel>
    suspend fun getByUsername(username: String): UserModel?
    suspend fun add(item: UserModel): Int?
    suspend fun save(item: UserModel): UserModel
    suspend fun getPushTokenById(id: Int): String?
    suspend fun savePushTokenWithUserId(id: Int, token: String)
}

class UserRepositoryDatabase : UserRepository {
    override suspend fun getAll(): List<UserModel> = dbQuery {
        Users.selectAll().map { toUserModel(it) }
    }

    override suspend fun getById(id: Int): UserModel? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getByIds(ids: Collection<Int>): List<UserModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getByUsername(username: String): UserModel? = dbQuery {
        Users.select { Users.username eq username }.mapNotNull { toUserModel(it) }.singleOrNull()
    }

    override suspend fun add(item: UserModel): Int? = dbQuery {
        Users.insert {
            it[username] = item.username
            it[password] = item.password
        }[Users.id]
    }

    override suspend fun save(item: UserModel): UserModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getPushTokenById(id: Int): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun savePushTokenWithUserId(id: Int, token: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun toUserModel(row: ResultRow): UserModel =
        UserModel(
            id = row[Users.id],
            username = row[Users.username],
            password = row[Users.password]
        )
}

