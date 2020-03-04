package ru.netology.dto

import ru.netology.model.PostType

data class PostRequestDto (
    val content: String,
    val address: String,
    val link: String,
    val type: PostType,
    val attachmentId: String
)