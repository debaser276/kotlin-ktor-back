package ru.netology.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.firstapp.dto.Post
import ru.netology.firstapp.dto.PostType
import ru.netology.firstapp.dto.x

class PostRepositoryInMemoryWithMutexImpl : PostRepository {
    private var nextId = 1
    private val posts = mutableListOf(
        Post(
            id = 1,
            author = "Author1",
            created = 1579950900,
            content = "Post1",
            likes = 8,
            comments = 11,
            shares = 5,
            commentedByAuthor = true,
            sharedByAuthor = true),
        Post(
            id = 2,
            author = "Author2",
            created = 1579950400,
            content = "Event1",
            address = "Somewhere",
            loc = 40.702807 x -73.990380,
            likes = 0,
            comments = 5,
            shares = 123,
            type = PostType.EVENT),
        Post(
            id = 3,
            author = "Author3",
            created = 1579940900,
            content = "Video1",
            link = "https://www.youtube.com/watch?v=Te1-tE4TVHA",
            likes = 5,
            comments = 17,
            shares = 78,
            likedByAuthor = true,
            type = PostType.VIDEO),
        Post(
            id = 4,
            author = "Author1",
            created = 1579950400,
            content = "Event2",
            address = "Somewhere",
            loc = 45.702807 x -78.990380,
            likes = 0,
            comments = 5,
            shares = 3,
            likedByAuthor = true,
            type = PostType.EVENT),
        Post(
            id = 5,
            author = "Author2",
            created = 1579950900,
            content = "Post2",
            likes = 1,
            comments = 21,
            shares = 6,
            commentedByAuthor = true),
        Post(
            id = 6,
            author = "Author3",
            created = 1579940900,
            content = "Video2",
            link = "https://www.youtube.com/watch?v=MYDuy7wM8Gk",
            likes = 5,
            comments = 7,
            shares = 8,
            type = PostType.VIDEO),
        Post(
            id = 7,
            author = "Author1",
            created = 1579950900,
            content = "Post3",
            likes = 3,
            comments = 1,
            shares = 3,
            commentedByAuthor = true,
            sharedByAuthor = true,
            likedByAuthor = true),
        Post(
            id = 8,
            author = "Author2",
            created = 1579950400,
            sourceId = 3,
            likes = 0,
            comments = 5,
            shares = 123,
            type = PostType.REPOST)
    )
    private val mutex = Mutex()

    override suspend fun getAll(): List<Post> {
        mutex.withLock {
            return posts.reversed()
        }
    }

    override suspend fun getById(id: Int): Post? {
        mutex.withLock {
            return posts.find { it.id == id }
        }
    }

    override suspend fun save(post: Post): Post {
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

    override suspend fun removeById(id: Int) {
        mutex.withLock {
            posts.removeIf { it.id == id }
        }
    }

    override suspend fun likeById(id: Int): Post? {
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

    override suspend fun dislikeById(id: Int): Post? {
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

    override suspend fun shareById(id: Int): Post? {
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