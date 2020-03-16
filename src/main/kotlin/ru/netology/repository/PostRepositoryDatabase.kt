package ru.netology.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import ru.netology.model.*
import ru.netology.repository.DatabaseFactory.dbQuery

class PostRepositoryDatabase : PostRepository {
    override suspend fun getAll(): List<PostModel> = dbQuery {
        Posts.selectAll().map { toPostModel(it) }
    }

    override suspend fun getById(id: Int): PostModel? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun save(post: PostModel): PostModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun removeById(id: Int): List<PostModel>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun likeById(id: Int, userId: Int): PostModel? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun dislikeById(id: Int, userId: Int): PostModel? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun shareById(id: Int): PostModel? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun toPostModel(row: ResultRow): PostModel = PostModel(
        id = row[Posts.id],
        authorId = row[Posts.authorId],
        author = row[Posts.author],
        created = row[Posts.created],
        content = row[Posts.content],
        address = row[Posts.address],
        loc = Location(row[Posts.lat], row[Posts.lng]),
        link = row[Posts.link],
        sourceId = row[Posts.sourceId],
        likes = row[Posts.likes],
        likedSet = if (row[Posts.likedSet].isNotEmpty()) row[Posts.likedSet].split(",").map { it.toInt() }.toMutableSet() else mutableSetOf(),
        reposts = row[Posts.reposts],
        repostedSet = if (row[Posts.repostedSet].isNotEmpty()) row[Posts.repostedSet].split(",").map { it.toInt() }.toMutableSet() else mutableSetOf(),
        shares = row[Posts.shares],
        sharedByMe = row[Posts.sharedByMe],
        views = row[Posts.views],
        type = PostType.valueOf(row[Posts.type]),
        attachment = AttachmentModel(row[Posts.attachmentId], MediaType.valueOf(row[Posts.mediaType]))
    )

    private suspend fun insertPost(post: PostModel) = dbQuery {
        Posts.insert {
            it[authorId] = post.authorId
            it[author] = post.author
            it[created] = post.created
            it[content] = post.content
            it[address] = post.address
            it[lat] = post.loc?.lat
            it[lng] = post.loc?.lng
            it[link] = post.link
            it[sourceId] = post.sourceId
            it[likes] = post.likes
            it[likedSet] = post.likedSet.joinToString(",")
            it[reposts] = post.reposts
            it[repostedSet] = post.repostedSet.joinToString(",")
            it[shares] = post.shares
            it[sharedByMe] = post.sharedByMe
            it[views] = post.views
            it[type] = post.type.toString()
            it[attachmentId] = post.attachment?.id
            it[mediaType] = post.attachment?.mediaType.toString()
        }
    }
}