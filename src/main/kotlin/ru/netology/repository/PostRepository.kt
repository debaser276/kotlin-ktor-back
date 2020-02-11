package ru.netology.repository

import ru.netology.model.Post

interface PostRepository {
    suspend fun getAll(): List<Post>
    suspend fun getById(id: Int): Post?
    suspend fun save(post: Post): Post
    suspend fun removeById(id: Int)
    suspend fun likeById(id: Int): Post?
    suspend fun dislikeById(id: Int): Post?
    suspend fun shareById(id: Int): Post?
}