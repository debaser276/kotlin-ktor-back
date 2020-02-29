package ru.netology.dto

import ru.netology.model.Location
import ru.netology.model.MediaType
import ru.netology.model.PostModel
import ru.netology.model.PostType

data class PostResponseDto (
    val id: Int,
    val author: String,
    val created: Long,
    val content: String?,
    val address: String? = null,
    val loc: Location? = null,
    val link: String? = null,
    val sourceId: Int? = null,
    val media: String? = null,
    val mediaType: MediaType? = null,
    var likes: Int = 0,
    val likedSet: MutableSet<Int> = mutableSetOf(),
    var reposts: Int = 0,
    var repostedByMe: Boolean = false,
    var sharedByMe: Boolean = false,
    var shares: Int = 0,
    var views: Int = 0,
    val type: PostType = PostType.POST
) {
    companion object {
        fun fromModel(post: PostModel) = PostResponseDto(
            id = post.id,
            author = post.author,
            created = post.created,
            content = post.content,
            address = post.address,
            loc = post.loc,
            link = post.link,
            sourceId = post.sourceId,
            likes = post.likes,
            likedSet = post.likedSet,
            reposts = post.reposts,
            repostedByMe = post.repostedByMe,
            shares = post.shares,
            sharedByMe = post.sharedByMe,
            views = post.views,
            type = post.type
        )
    }
}