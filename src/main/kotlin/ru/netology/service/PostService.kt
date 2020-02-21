package ru.netology.service

import io.ktor.features.NotFoundException
import ru.netology.dto.PostRequestDto
import ru.netology.dto.PostResponseDto
import ru.netology.exception.ForbiddenException
import ru.netology.model.PostModel
import ru.netology.model.PostType
import ru.netology.repository.PostRepository

class PostService( private val repo: PostRepository) {
    suspend fun getAll(): List<PostResponseDto> = repo.getAll()
        .map { PostResponseDto.fromModel(it) }

    suspend fun getById(id: Int): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model)
    }

    suspend fun likeById(id: Int, userId: Int): PostResponseDto {
        val model = repo.likeById(id, userId) ?: throw NotFoundException()
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun dislikeById(id: Int, userId: Int): PostResponseDto {
        val model = repo.dislikeById(id, userId) ?: throw NotFoundException()
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun shareById(id: Int): PostResponseDto {
        val model = repo.shareById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun editPost(id: Int, input: PostRequestDto, username: String): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        if (username == model.author) {
            return PostResponseDto.fromModel(repo.save(model.copy(
                content = input.content,
                address = input.address,
                link = input.link)))
            } else {
            throw ForbiddenException()
        }
    }

    suspend fun repost(id: Int, username: String): PostResponseDto {
        repo.getById(id) ?: throw NotFoundException()
        val repost = PostModel(author = username, sourceId = id, type = PostType.REPOST)
        return PostResponseDto.fromModel(repo.save(repost))
    }

    suspend fun post(input: PostRequestDto, username: String): PostResponseDto {
        val model = PostModel(
            author = username,
            content = input.content,
            type = input.type
        )
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun removeById(id: Int, username: String): List<PostResponseDto> {
        val model = repo.getById(id) ?: throw NotFoundException()
        if (model.author == username) repo.removeById(id)
        else throw ForbiddenException()
        return repo.getAll().map { PostResponseDto.fromModel(it) }
    }
}