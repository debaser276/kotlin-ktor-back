package ru.netology.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.firstapp.dto.Post

class PostRepositoryInMemoryWithMutexImpl : PostRepository {
    private var nextId = 1L
    private val items = mutableListOf<Post>()
    private val mutex = Mutex()

    override suspend fun getAll(): List<Post> {
        mutex.withLock {
            return items.reversed()
        }
    }
}