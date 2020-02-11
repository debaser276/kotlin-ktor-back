package ru.netology.dto

import ru.netology.model.PostType

data class PostRequestDto (
    val id: Int,
    val author: String,
    val created: Int,
    val type: PostType
)