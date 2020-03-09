package ru.netology.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.model.UserModel

class UserRepositoryInMemoryWithMutex : UserRepository {
    private var nextId = 1
    private val items = mutableListOf<UserModel>()
    private val mutex = Mutex()
    private val pushTokenWithUserIdMap = mutableMapOf<Int, String>()

    override suspend fun getAll(): List<UserModel> = items.toList()

    override suspend fun getById(id: Int): UserModel? = items.find { it.id == id }

    override suspend fun getByIds(ids: Collection<Int>): List<UserModel> = items.filter { ids.contains(it.id) }

    override suspend fun getByUsername(username: String): UserModel? = items.find { it.username == username }

    override suspend fun savePushTokenWithUserId(id: Int, token: String) {
        pushTokenWithUserIdMap[id] = token
    }

    override suspend fun add(item: UserModel): UserModel {
        mutex.withLock {
            val copy = item.copy(id = nextId++)
            items.add(copy)
            return copy
        }
    }

    override suspend fun save(item: UserModel): UserModel {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == item.id }) {
                -1 -> {
                    val copy = item.copy(id = nextId++)
                    items.add(copy)
                    copy
                }
                else -> {
                    val copy = items[index].copy(username = item.username, password = item.password)
                    items[index] = copy
                    copy
                }
            }
        }
    }
}