package ru.netology.repository

import ru.netology.firstapp.dto.Post

interface PostRepository {
    suspend fun getAll() :List<Post>
}