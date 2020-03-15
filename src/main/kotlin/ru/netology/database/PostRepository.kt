package ru.netology.database

import ru.netology.model.PostModel

interface PostRepository {
    suspend fun getAll(): List<PostModel>
    suspend fun getById(id: Int): PostModel?
    suspend fun save(post: PostModel): PostModel
    suspend fun removeById(id: Int): List<PostModel>?
    suspend fun likeById(id: Int, userId: Int): PostModel?
    suspend fun dislikeById(id: Int, userId: Int): PostModel?
    suspend fun shareById(id: Int): PostModel?
}