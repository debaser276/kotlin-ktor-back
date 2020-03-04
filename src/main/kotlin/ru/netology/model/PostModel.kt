package ru.netology.model

enum class PostType {
    POST, VIDEO, EVENT, AD, REPOST
}

class Location(val lat: Double, val lng: Double)

infix fun Double.x(that: Double) = Location(this, that)

data class PostModel (
    val id: Int = 0,
    val authorId: Int,
    val author: String,
    val created: Long = System.currentTimeMillis() / 1000,
    val content: String? = null,
    val address: String? = null,
    val loc: Location? = null,
    val link: String? = null,
    val sourceId: Int? = null,
    var likes: Int = 0,
    var likedSet: MutableSet<Int> = mutableSetOf(),
    var reposts: Int = 0,
    var repostedSet: MutableSet<Int> = mutableSetOf(),
    var shares: Int = 0,
    var sharedByMe: Boolean = false,
    var views: Int = 0,
    val type: PostType = PostType.POST,
    val attachmentModel: AttachmentModel? = null
)