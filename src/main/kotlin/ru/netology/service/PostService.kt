package ru.netology.service

import io.ktor.application.ApplicationCall
import io.ktor.features.NotFoundException
import ru.netology.dto.PostRequestDto
import ru.netology.dto.PostResponseDto
import ru.netology.model.PostModel
import ru.netology.model.PostType
import ru.netology.repository.PostRepository
import ru.netology.route.id

class PostService( private val repo: PostRepository) {
    suspend fun getAll(username: String): List<PostResponseDto> = repo.getAll()
        .filter { it.author == username }
        .map { PostResponseDto.fromModel(it) }

    suspend fun getById(id: Int): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model)
    }

    suspend fun likeById(id: Int): PostResponseDto {
        val model = repo.likeById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun dislikeById(id: Int): PostResponseDto {
        val model = repo.dislikeById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun shareById(id: Int): PostResponseDto {
        val model = repo.shareById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun repost(id: Int): PostResponseDto {
        repo.getById(id) ?: throw NotFoundException()
        val repost = PostModel(author = "User", sourceId = id, type = PostType.REPOST)
        return PostResponseDto.fromModel(repo.save(repost))
    }

    suspend fun post(input: PostRequestDto, author: String): PostResponseDto {
        val post = PostModel(
            author = author,
            content = input.content,
            type = input.type
        )
        return PostResponseDto.fromModel(repo.save(post))
    }

    suspend fun removeById(id: Int): List<PostResponseDto> {
        repo.removeById(id) ?: throw NotFoundException()
        return repo.getAll().map { PostResponseDto.fromModel(it) }
    }
}