package ru.netology.route

import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import ru.netology.dto.PostRequestDto
import ru.netology.dto.PostResponseDto
import ru.netology.model.Post
import ru.netology.model.PostType
import ru.netology.repository.PostRepository

fun Routing.v1() {
    route("/api/v1/posts") {
        val repo by kodein().instance<PostRepository>()
        get {
            val response = repo.getAll().map { PostResponseDto.fromModel(it) }
            call.respond(response)
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Long")
            val post = repo.getById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(post)
            call.respond(response)
        }
        post("/{id}/like") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Long")
            val post = repo.likeById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(repo.save(post))
            call.respond(response)
        }
        post("/{id}/dislike") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Long")
            val post = repo.dislikeById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(repo.save(post))
            call.respond(response)
        }
        post("/{id}/share") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Long")
            val post = repo.shareById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(repo.save(post))
            call.respond(response)
        }
        post("/{id}/repost") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Long")
            val repost = Post(id = 0, author = "User", created = System.currentTimeMillis() / 1000, sourceId = id, type = PostType.REPOST)
            val response = PostResponseDto.fromModel(repo.save(repost))
            call.respond(response)
        }
        post {
            val input = call.receive<PostRequestDto>()
            val post = Post(
                author = input.author,
                type = input.type
            )
            val response = PostResponseDto.fromModel(repo.save(post))
            call.respond(response)
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Long")
            val post = repo.likeById(id) ?: throw NotFoundException()
            repo.removeById(id)
        }
    }
}