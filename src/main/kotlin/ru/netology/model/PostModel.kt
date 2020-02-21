package ru.netology.model

enum class PostType {
    POST, VIDEO, EVENT, AD, REPOST
}

class Location(val lat: Double, val lng: Double)

infix fun Double.x(that: Double) = Location(this, that)

data class PostModel (
    val id: Int = 0,
    val author: String,
    val created: Long = System.currentTimeMillis() / 1000,
    val content: String? = null,
    val address: String? = null,
    val loc: Location? = null,
    val link: String? = null,
    val sourceId: Int? = null,
    val media: String? = null,
    val mediaType: MediaType? = null,
    var likes: Int = 0,
    var likedSet: MutableSet<Int> = mutableSetOf(),
    var comments: Int = 0,
    var shares: Int = 0,
    var views: Int = 0,
    var likedByAuthor: Boolean = false,
    var commentedByAuthor: Boolean = false,
    var sharedByAuthor: Boolean = false,
    val type: PostType = PostType.POST
)