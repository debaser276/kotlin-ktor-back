package ru.netology.route

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import ru.netology.dto.*
import ru.netology.model.PostModel
import ru.netology.model.PostType
import ru.netology.model.UserModel
import ru.netology.repository.PostRepository
import ru.netology.service.FileService
import ru.netology.service.UserService

val <T: Any> PipelineContext<T, ApplicationCall>.id
    get() = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Long")

class RoutingV1(
    private val staticPath: String,
    private val fileService: FileService,
    private val userService: UserService
) {
    fun setup(configuration: Routing) {
        with(configuration) {
            route("/api/v1") {
                static("/static") {
                    files(staticPath)
                }

                post("/registration") {
                    val input = call.receive<AuthenticationRequestDto>()
                    when(userService.getByUsername(input.username)) {
                        null -> call.respond(userService.registrate(input))
                        else -> call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Пользователь с таким логином уже зарегистрирован"))
                    }
                }

                post("/authentication") {
                    val input = call.receive<AuthenticationRequestDto>()
                    val response = userService.authenticate(input)
                    call.respond(response)
                }

                authenticate {
                    route("/me") {
                        get {
                            val me = call.authentication.principal<UserModel>()
                            call.respond(UserResponseDto.fromModel(me!!))
                        }
                    }
                    route("/posts") {
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