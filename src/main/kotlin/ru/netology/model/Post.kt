package ru.netology.firstapp.dto

enum class PostType {
    POST, VIDEO, EVENT, AD
}

open class Post (
    val id: Int,
    val author: String,
    val created: Long,
    val content: String,
    val address: String? = null,
    val loc: Location? = null,
    val link: String? = null,
    var likes: Int = 0,
    var comments: Int = 0,
    var shares: Int = 0,
    var likedByAuthor: Boolean = false,
    var commentedByAuthor: Boolean = false,
    var sharedByAuthor: Boolean = false,
    val type: PostType = PostType.POST
)