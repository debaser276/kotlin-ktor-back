package ru.netology.model

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Posts: Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val authorId: Column<Int> = integer("authorId")
    val author: Column<String> = varchar("author", 20)
    val created: Column<Long> = long("created")
    val content: Column<String?> = text("content").nullable()
    val address: Column<String?> = text("address").nullable()
    val lat: Column<Double?> = double("lat").nullable()
    val lng: Column<Double?> = double("lng").nullable()
    val link: Column<String?> = text("link").nullable()
    val sourceId: Column<Int?> = integer("SourceId").nullable()
    val likes: Column<Int> = integer("likes")
    val likedSet: Column<String> = text("likedSet").default("")
    val reposts: Column<Int> = integer("reposts")
    val repostedSet: Column<String> = text("repostedSet").default("")
    val shares: Column<Int> = integer("shares")
    val sharedByMe: Column<Boolean> = bool("sharedByMe")
    val views: Column<Int> = integer("views")
    val type: Column<String> = varchar("type", 10)
    val attachmentId: Column<String?> = text("attachmentId").nullable()
    val mediaType: Column<String> = varchar("mediaType", 10).default("")

}

enum class PostType {
    POST, VIDEO, EVENT, AD, REPOST
}

class Location(val lat: Double?, val lng: Double?)

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
    val attachment: AttachmentModel? = null
)