package ru.netology.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.model.PostModel

class PostRepositoryInMemoryWithMutexImpl : PostRepository {
    private var nextId = 1
    private val posts = mutableListOf<PostModel>()
    private val mutex = Mutex()

    override suspend fun getAll(): List<PostModel> {
        mutex.withLock {
            posts.onEach { it.views++ }
            return posts.reversed()
        }
    }

    override suspend fun getById(id: Int): PostModel? = posts.find { it.id == id }

    override suspend fun save(post: PostModel): PostModel {
        mutex.withLock {
            return when (val index = posts.indexOfFirst { it.id == post.id }) {
                -1 -> {
                    val copy = post.copy(id = nextId++)
                    posts.add(copy)
                    copy
                }
                else -> {
                    posts[index] = post
                    post
                }
            }
        }
    }

    override suspend fun removeById(id: Int): List<PostModel>? {
        mutex.withLock {
            return when(val index = posts.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    posts.removeIf { it.id == id }
                    posts.reversed()
                }
            }
        }
    }

    override suspend fun likeById(id: Int): PostModel? {
        mutex.withLock {
            return when (val index = posts.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    val post = posts[index]
                    val copy = post.copy(likes = post.likes + 1)
                    try {
                        posts[index]
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        println("size: ${posts.size}")
                        println(index)
                    }
                    copy
                }
            }
        }
    }

    override suspend fun dislikeById(id: Int): PostModel? {
        mutex.withLock {
            return when (val index = posts.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    val post = posts[index]
                    val copy = post.copy(likes = if (post.likes == 0) 0 else post.likes - 1)
                    try {
                        posts[index]
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        println("size: ${posts.size}")
                        println(index)
                    }
                    copy
                }
            }
        }
    }

    override suspend fun shareById(id: Int): PostModel? {
        mutex.withLock {
            return when (val index = posts.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    val post = posts[index]
                    val copy = post.copy(shares = post.shares + 1)
                    try {
                        posts[index]
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        println("size: ${posts.size}")
                        println(index)
                    }
                    copy
                }
            }
        }
    }
}