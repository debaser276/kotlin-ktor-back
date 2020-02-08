package ru.netology.dto

import ru.netology.firstapp.dto.Location
import ru.netology.firstapp.dto.Post
import ru.netology.firstapp.dto.PostType

data class PostResponseDto (
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
) {
    companion object {
        fun fromModel(post: Post) = PostResponseDto(
            id = post.id,
            author = post.author,
            created = post.created,
            content = post.content,
            address = post.address,
            loc = post.loc,
            link = post.link,
            likes = post.likes,
            comments = post.comments,
            shares = post.shares,
            likedByAuthor = post.likedByAuthor,
            commentedByAuthor = post.commentedByAuthor,
            sharedByAuthor = post.sharedByAuthor,
            type = post.type
        )
    }
}