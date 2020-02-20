package ru.netology.dto

import ru.netology.model.PostType

data class PostRequestDto (
    val content: String?,
    val type: PostType
)