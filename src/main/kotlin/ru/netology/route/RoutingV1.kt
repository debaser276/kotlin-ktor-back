package ru.netology.route

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import ru.netology.dto.PostRequestDto
import ru.netology.dto.PostResponseDto
import ru.netology.model.PostModel
import ru.netology.model.PostType
import ru.netology.repository.PostRepository
import ru.netology.service.FileService

class RoutingV1(
    private val staticPath: String,
    private val fileService: FileService
) {
    fun setup(configuration: Routing) {
        with(configuration) {
            route("/api/v1") {
                static("/static") {
                    files(staticPath)
                }

                route("/media") {
                    post {
                        val multipart = call.receiveMultipart()
                        val response = fileService.save(multipart)
                        call.respond(response)
                    }
                }
            }
        }
    }
}

val <T: Any> PipelineContext<T, ApplicationCall>.id
    get() = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Long")

fun Routing.v1() {
    route("/api/v1/posts") {
        val repo by kodein().instance<PostRepository>()
        get {
            val response = repo.getAll().map { PostResponseDto.fromModel(it) }
            call.respond(response)
        }
        get("/{id}") {
            val post = repo.getById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(post)
            call.respond(response)
        }
        post("/{id}/like") {
            val post = repo.likeById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(repo.save(post))
            call.respond(response)
        }
        post("/{id}/dislike") {
            val post = repo.dislikeById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(repo.save(post))
            call.respond(response)
        }
        post("/{id}/share") {
            val post = repo.shareById(id) ?: throw NotFoundException()
            val response = PostResponseDto.fromModel(repo.save(post))
            call.respond(response)
        }
        post("/{id}/repost") {
            repo.getById(id) ?: throw NotFoundException()
            val repost = PostModel(author = "User", sourceId = id, type = PostType.REPOST)
            val response = PostResponseDto.fromModel(repo.save(repost))
            call.respond(response)
        }
        post {
            val input = call.receive<PostRequestDto>()
            val post = PostModel(
                author = input.author,
                type = input.type
            )
            val response = PostResponseDto.fromModel(repo.save(post))
            call.respond(response)
        }
        delete("/{id}") {
            val response = repo.removeById(id) ?: throw NotFoundException()
            call.respond(response)
        }
    }
}