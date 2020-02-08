package ru.netology.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.firstapp.dto.Post
import ru.netology.firstapp.dto.PostType
import ru.netology.firstapp.dto.x

class PostRepositoryInMemoryWithMutexImpl : PostRepository {
    private var nextId = 1L
    private val posts = mutableListOf(
        Post(
            id = 1,
            author = "Netology",
            created = 1579950900,
            content = "Post1",
            likes = 8,
            comments = 11,
            shares = 5,
            commentedByAuthor = true,
            sharedByAuthor = true),
        Post(
            id = 2,
            author = "Netology",
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
            author = "Netology",
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
            author = "Netology",
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
            author = "Netology",
            created = 1579950900,
            content = "Post2",
            likes = 1,
            comments = 21,
            shares = 6,
            commentedByAuthor = true),
        Post(
            id = 6,
            author = "Netology",
            created = 1579940900,
            content = "Video2",
            link = "https://www.youtube.com/watch?v=MYDuy7wM8Gk",
            likes = 5,
            comments = 7,
            shares = 8,
            type = PostType.VIDEO),
        Post(
            id = 7,
            author = "Netology",
            created = 1579950900,
            content = "Post3",
            likes = 3,
            comments = 1,
            shares = 3,
            commentedByAuthor = true,
            sharedByAuthor = true,
            likedByAuthor = true)
    )
    private val ads = listOf(
        Post(
            id = 8,
            author = "Netology",
            created = 1579950900,
            content = "Ad1",
            link = "https://ru.wikipedia.org/wiki/%D0%A0%D0%B5%D0%BA%D0%BB%D0%B0%D0%BC%D0%B0",
            type = PostType.AD),
        Post(
            id = 9,
            author = "Netology",
            created = 1579950900,
            content = "Ad2",
            link = "https://ru.wikipedia.org/wiki/%D0%A0%D0%B5%D0%BA%D0%BB%D0%B0%D0%BC%D0%B0",
            type = PostType.AD),
        Post(
            id = 10,
            author = "Netology",
            created = 1579950900,
            content = "Ad3",
            link = "https://ru.wikipedia.org/wiki/%D0%A0%D0%B5%D0%BA%D0%BB%D0%B0%D0%BC%D0%B0",
            type = PostType.AD)
    )
    private val mutex = Mutex()

    override suspend fun getAll(): List<Post> {
        mutex.withLock {
            return posts.reversed()
        }
    }

    override suspend fun getAds(): List<Post> {
        mutex.withLock {
            return ads
        }
    }
}