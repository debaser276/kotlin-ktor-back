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
import ru.netology.service.PostService
import ru.netology.service.UserService

val <T: Any> PipelineContext<T, ApplicationCall>.id
    get() = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Long")

val <T: Any> PipelineContext<T, ApplicationCall>.me
    get() = call.authentication.principal<UserModel>()

class RoutingV1(
    private val staticPath: String,
    private val fileService: FileService,
    private val userService: UserService,
    private val postService: PostService
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
                        get {
                            val response = postService.getAll(me!!.username)
                            call.respond(response)
                        }
                        get("/{id}") {
                            val response = postService.getById(id)
                            call.respond(response)
                        }
                        post("/{id}/like") {
                            val response = postService.likeById(id)
                            call.respond(response)
                        }
                        post("/{id}/dislike") {
                            val response = postService.dislikeById(id)
                            call.respond(response)
                        }
                        post("/{id}/share") {
                            val response = postService.shareById(id)
                            call.respond(response)
                        }
                        post("/{id}/repost") {
                            val response = postService.repost(id)
                            call.respond(response)
                        }
                        post {
                            val input = call.receive<PostRequestDto>()
                            val response = postService.post(input, me!!.username)
                            call.respond(response)
                        }
                        delete("/{id}") {
                            val response = postService.removeById(id)
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