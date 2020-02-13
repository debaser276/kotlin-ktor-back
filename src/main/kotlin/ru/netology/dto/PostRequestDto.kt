package ru.netology.dto

import ru.netology.model.PostType

data class PostRequestDto (
    val author: String,
    val type: PostType
)