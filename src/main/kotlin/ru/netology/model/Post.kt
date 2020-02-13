package ru.netology.model

enum class PostType {
    POST, VIDEO, EVENT, AD, REPOST
}

data class Post (
    val id: Int = 0,
    val author: String,
    val created: Long = System.currentTimeMillis() / 1000,
    val content: String? = null,
    val address: String? = null,
    val loc: Location? = null,
    val link: String? = null,
    val sourceId: Int? = null,
    var likes: Int = 0,
    var comments: Int = 0,
    var shares: Int = 0,
    var views: Int = 0,
    var likedByAuthor: Boolean = false,
    var commentedByAuthor: Boolean = false,
    var sharedByAuthor: Boolean = false,
    val type: PostType = PostType.POST
)