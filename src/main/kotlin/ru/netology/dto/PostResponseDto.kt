package ru.netology.dto

import ru.netology.model.Location
import ru.netology.model.PostModel
import ru.netology.model.PostType

data class PostResponseDto (
    val id: Int,
    val authorId: Int,
    val author: String,
    val created: Long,
    val content: String?,
    val address: String? = null,
    val loc: Location? = null,
    val link: String? = null,
    val sourceId: Int? = null,
    var likes: Int = 0,
    val likedSet: MutableSet<Int> = mutableSetOf(),
    var reposts: Int = 0,
    var repostedSet: MutableSet<Int> = mutableSetOf(),
    var sharedByMe: Boolean = false,
    var shares: Int = 0,
    var views: Int = 0,
    val type: PostType = PostType.POST,
    val attachment: AttachmentResponseDto?
) {
    companion object {
        fun fromModel(post: PostModel) = PostResponseDto(
            id = post.id,
            authorId = post.authorId,
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
            repostedSet = post.repostedSet,
            shares = post.shares,
            sharedByMe = post.sharedByMe,
            views = post.views,
            type = post.type,
            attachment = post.attachment?.let { AttachmentResponseDto.fromModel(post.attachment) }
        )
    }
}