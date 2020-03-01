package ru.netology.service

import io.ktor.features.NotFoundException
import ru.netology.dto.PostRequestDto
import ru.netology.dto.PostResponseDto
import ru.netology.exception.ForbiddenException
import ru.netology.exception.PostNotFoundException
import ru.netology.model.PostModel
import ru.netology.model.PostType
import ru.netology.repository.PostRepository

class PostService(private val repo: PostRepository) {
    suspend fun getAll(): List<PostResponseDto> = repo.getAll()
        .map { PostResponseDto.fromModel(it) }

    suspend fun getById(id: Int): PostResponseDto {
        val model = repo.getById(id) ?: throw PostNotFoundException()
        return PostResponseDto.fromModel(model)
    }

    suspend fun likeById(id: Int, userId: Int): PostResponseDto {
        val model = repo.likeById(id, userId) ?: throw PostNotFoundException()
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun dislikeById(id: Int, userId: Int): PostResponseDto {
        val model = repo.dislikeById(id, userId) ?: throw PostNotFoundException()
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun shareById(id: Int): PostResponseDto {
        val model = repo.shareById(id) ?: throw PostNotFoundException()
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun editPost(id: Int, input: PostRequestDto, username: String): PostResponseDto {
        val model = repo.getById(id) ?: throw PostNotFoundException()
        if (username == model.author) {
            return PostResponseDto.fromModel(repo.save(model.copy(
                content = input.content,
                address = input.address,
                link = input.link)))
            } else {
            throw ForbiddenException()
        }
    }

    suspend fun repost(sourceId: Int, authorId: Int, author: String, content: String): PostResponseDto {
        val sourcePost = repo.getById(sourceId) ?: throw PostNotFoundException()
        PostResponseDto.fromModel(repo.save(PostModel(
            authorId = authorId,
            author = author,
            sourceId = sourceId,
            content = "Repost of $content (SourceId: $sourceId)",
            type = PostType.REPOST
        )))
        return PostResponseDto.fromModel(repo.save(sourcePost.copy(
            reposts = sourcePost.reposts + 1,
            repostedSet = sourcePost.repostedSet.apply { add(authorId) }
        )))
    }

    suspend fun post(input: PostRequestDto, authorId: Int, username: String): PostResponseDto {
        val model = PostModel(
            authorId = authorId,
            author = username,
            content = input.content,
            type = input.type
        )
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun removeById(id: Int, username: String): List<PostResponseDto> {
        val model = repo.getById(id) ?: throw PostNotFoundException()
        if (model.author == username) repo.removeById(id)
        else throw ForbiddenException()
        return repo.getAll().map { PostResponseDto.fromModel(it) }
    }
}