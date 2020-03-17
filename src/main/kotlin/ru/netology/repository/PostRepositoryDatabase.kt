package ru.netology.repository

import org.jetbrains.exposed.sql.*
import ru.netology.exception.AlreadyLikedException
import ru.netology.exception.DatabaseException
import ru.netology.model.*
import ru.netology.repository.DatabaseFactory.dbQuery

interface PostRepository {
    suspend fun getAll(): List<PostModel>
    suspend fun getById(id: Int): PostModel?
    suspend fun save(post: PostModel): Int
    suspend fun removeById(id: Int)
    suspend fun likeById(id: Int, userId: Int)
    suspend fun dislikeById(id: Int, userId: Int)
}

class PostRepositoryDatabase : PostRepository {
    override suspend fun getAll(): List<PostModel> = dbQuery {
        Posts.selectAll().map {
            toPostModel(it)
        }
    }

    override suspend fun getById(id: Int): PostModel? = dbQuery {
        Posts.select { Posts.id eq id }.map { toPostModel(it) }.singleOrNull()
    }

    override suspend fun save(post: PostModel): Int = dbQuery {
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
        }[Posts.id] ?: throw DatabaseException()
    }

    override suspend fun removeById(id: Int): Unit = dbQuery {
        Posts.deleteWhere { Posts.id eq id }
    }

    override suspend fun likeById(id: Int, userId: Int): Unit = dbQuery {
        val likedSet = Posts.select { Posts.id eq id }.map { toPostModel(it) }.single().likedSet
        if (!likedSet.contains(userId)) {
            likedSet.add(userId)
            Posts.update({ Posts.id eq id }) {
                it[Posts.likedSet] = likedSet.joinToString(",")
                with(SqlExpressionBuilder) {
                    it.update(likes, likes + 1)
                }
            }
            Posts.select { Posts.id eq id }.map { toPostModel(it) }.singleOrNull()
        } else throw AlreadyLikedException()
    }

    override suspend fun dislikeById(id: Int, userId: Int): Unit = dbQuery {
        val likedSet = Posts.select { Posts.id eq id }.map { toPostModel(it) }.single().likedSet
        if (likedSet.contains(userId)) {
            likedSet.remove(userId)
            Posts.update({ Posts.id eq id }) {
                it[Posts.likedSet] = likedSet.joinToString(",")
                with(SqlExpressionBuilder) {
                    it.update(likes, likes - 1)
                }
            }
            Posts.select { Posts.id eq id }.map { toPostModel(it) }.singleOrNull()
        } else throw AlreadyLikedException()
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
        attachment = AttachmentModel(row[Posts.attachmentId], MediaType.valueOf(if (row[Posts.mediaType].equals("null")) "NOMEDIA" else row[Posts.mediaType]))
    )
}