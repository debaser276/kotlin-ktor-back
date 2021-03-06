package ru.netology.service

import ru.netology.dto.PostRequestDto
import ru.netology.dto.PostResponseDto
import ru.netology.exception.*
import ru.netology.model.AttachmentModel
import ru.netology.model.MediaType
import ru.netology.model.PostModel
import ru.netology.model.PostType
import ru.netology.repository.PostRepository

class PostService(private val repo: PostRepository, private val resultSize: Int) {
    suspend fun getAll(): List<PostResponseDto> = repo.getAll()
        .map { PostResponseDto.fromModel(it) }

    suspend fun getRecent(): List<PostResponseDto> {
        return getAll().take(resultSize)
    }

    suspend fun getBefore(id: Int): List<PostResponseDto> {
        return getAll().asSequence().filter { it.id < id }.take(resultSize).toList()
    }

    suspend fun getAfter(id: Int): List<PostResponseDto> {
        if (repo.getAll().isEmpty()) throw NoPostsException()
        return getAll().asSequence().filter { it.id > id }.take(resultSize).toList()
    }

    suspend fun getById(id: Int): PostResponseDto {
        val model = repo.getById(id) ?: throw PostNotFoundException()
        return PostResponseDto.fromModel(model)
    }

    suspend fun getModelById(id: Int): PostModel =
        repo.getById(id) ?: throw PostNotFoundException()

    suspend fun likeById(id: Int, userId: Int): PostResponseDto {
        val post = repo.getById(id) ?: throw PostNotFoundException()
        if (post.likedSet.contains(userId)) throw AlreadyLikedException()
        repo.likeById(id, userId)
        return PostResponseDto.fromModel(getModelById(id))
    }

    suspend fun dislikeById(id: Int, userId: Int): PostResponseDto {
        val post = repo.getById(id) ?: throw PostNotFoundException()
        if (!post.likedSet.contains(userId)) throw NotLikedYetException()
        repo.dislikeById(id, userId)
        return PostResponseDto.fromModel(getModelById(id))
    }

    suspend fun editPost(id: Int, input: PostRequestDto, username: String): PostResponseDto {
        val model = repo.getById(id) ?: throw PostNotFoundException()
        if (username == model.author) {
            return PostResponseDto.fromModel(getModelById(repo.save(model.copy(
                content = input.content,
                address = input.address,
                link = input.link))))
        } else throw ForbiddenException()
    }

    suspend fun repost(sourceId: Int, authorId: Int, author: String, content: String): PostResponseDto {
        val sourcePost = repo.getById(sourceId) ?: throw PostNotFoundException()
        if (sourcePost.repostedSet.contains(authorId)) throw AlreadyRepostedException()
        repo.addRepost(sourceId, authorId)
        return PostResponseDto.fromModel(getModelById(repo.save(PostModel(
            authorId = authorId,
            author = author,
            sourceId = sourceId,
            content = "Repost of SourceId: $sourceId. ${sourcePost.content}",
            type = PostType.REPOST
        ))))
    }

    suspend fun post(input: PostRequestDto, authorId: Int, username: String): PostResponseDto {
        val model = PostModel(
            authorId = authorId,
            author = username,
            content = input.content,
            type = input.type,
            attachment = input.attachmentId?.let {
                AttachmentModel(input.attachmentId, mediaType = MediaType.IMAGE)
            }
        )
        return PostResponseDto.fromModel(getModelById(repo.save(model)))
    }

    suspend fun removeById(id: Int, username: String): List<PostResponseDto> {
        val model = repo.getById(id) ?: throw PostNotFoundException()
        if (model.author == username) repo.removeById(id)
        else throw ForbiddenException()
        return repo.getAll().map { PostResponseDto.fromModel(it) }
    }
}