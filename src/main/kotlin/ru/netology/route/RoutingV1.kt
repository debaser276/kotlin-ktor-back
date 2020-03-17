package ru.netology.route

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.ParameterConversionException
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.Logger
import ru.netology.dto.*
import ru.netology.exception.LoginAlreadyExistsException
import ru.netology.model.UserModel
import ru.netology.service.FCMService
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
    private val postService: PostService,
    private val fcmService: FCMService,
    private val logger: Logger
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
                        null -> call.respond(userService.register(input))
                        else -> throw LoginAlreadyExistsException()
                    }
                }

                post("/authentication") {
                    val input = call.receive<AuthenticationRequestDto>()
                    val response = userService.authenticate(input)
                    call.respond(response)
                }

                authenticate {
                    route("/push") {
                        post() {
                            val input = call.receive<PushRequestDto>()
                            userService.savePushTokenWithUserID(me!!.id, input.token)
                            fcmService.sendWelcome(me!!.username, input.token)
                            call.respond(HttpStatusCode.OK)
                        }
                    }
                    route("/me") {
                        get {
                            val me = call.authentication.principal<UserModel>()
                            call.respond(UserResponseDto.fromModel(me!!))
                        }
                    }
                    route("/posts") {
                        get {
                            val response = postService.getAll()
                            call.respond(response)
                        }
                        get("/recent") {
                            call.respond(postService.getRecent())
                        }
                        get("/{id}/before") {
                            call.respond(postService.getBefore(id))
                        }
                        get("/{id}/after") {
                            call.respond(postService.getAfter(id))
                        }
                        get("/{id}") {
                            val response = postService.getById(id)
                            call.respond(response)
                        }
                        put("/{id}/like") {
                            val response = postService.likeById(id, me!!.id)
                            if (me!!.id != response.authorId) {
                                val pushToken = userService.getPushTokenById(response.authorId)
                                fcmService.sendLikeAdd(
                                    me!!.username,
                                    response.id,
                                    pushToken,
                                    response.content?.take(10)
                                )
                            }
                            call.respond(response)
                        }
                        put("/{id}/dislike") {
                            val response = postService.dislikeById(id, me!!.id)
                            call.respond(response)
                        }
                        patch("/{id}") {
                            val input = call.receive<PostRequestDto>()
                            val response = postService.editPost(id, input, me!!.username)
                            call.respond(response)
                        }
                        post("/{id}/repost") {
                            val input = call.receive<PostRequestDto>()
                            val response = postService.repost(id, me!!.id, me!!.username, input.content)
                            call.respond(response)
                        }
                        post {
                            val input = call.receive<PostRequestDto>()
                            val response = postService.post(input, me!!.id, me!!.username)
                            call.respond(response)
                        }
                        delete("/{id}") {
                            val response = postService.removeById(id, me!!.username)
                            call.respond(response)
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
}