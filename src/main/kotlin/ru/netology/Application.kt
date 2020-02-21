package ru.netology

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.cio.EngineMain
import org.kodein.di.generic.*
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import ru.netology.dto.ErrorResponseDto
import ru.netology.exception.AlreadyLikedException
import ru.netology.exception.ForbiddenException
import ru.netology.exception.InvalidPasswordException
import ru.netology.exception.NotLikedYetException
import ru.netology.repository.PostRepository
import ru.netology.repository.PostRepositoryInMemoryWithMutexImpl
import ru.netology.repository.UserRepository
import ru.netology.repository.UserRepositoryInMemoryWithMutex
import ru.netology.route.RoutingV1
import ru.netology.service.FileService
import ru.netology.service.JWTTokenService
import ru.netology.service.PostService
import ru.netology.service.UserService
import javax.naming.ConfigurationException

fun main(args : Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    install(StatusPages) {
        exception<InvalidPasswordException> {e ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponseDto("Wrong password"))
            throw e
        }
        exception<ForbiddenException>() {e ->
            call.respond(HttpStatusCode.Forbidden, ErrorResponseDto("Access denied!"))
            throw e
        }
        exception<AlreadyLikedException> {e ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("This post have already been liked"))
            throw e
        }
        exception<NotLikedYetException> {e ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("This post haven't been liked yet"))
            throw e
        }
        exception<NotFoundException> {e ->
            call.respond(HttpStatusCode.NotFound)
            throw e
        }
        exception<ParameterConversionException> {e ->
            call.respond(HttpStatusCode.BadRequest)
            throw e
        }
        exception<NotImplementedError> {e ->
            call.respond(HttpStatusCode.NotImplemented)
            throw e
        }
        exception<Throwable> {e ->
            call.respond(HttpStatusCode.InternalServerError)
            throw e
        }
    }

    install(KodeinFeature) {
        constant(tag = "upload-dir") with (
                environment.config.propertyOrNull("static.upload.dir")?.getString() ?:
                    throw ConfigurationException("Upload dir is not specified"))
        bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
        bind<JWTTokenService>() with eagerSingleton { JWTTokenService() }
        bind<FileService>() with eagerSingleton { FileService(instance(tag = "upload-dir")) }
        bind<PostRepository>() with singleton { PostRepositoryInMemoryWithMutexImpl() }
        bind<PostService>() with eagerSingleton { PostService(instance()) }
        bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithMutex() }
        bind<UserService>() with eagerSingleton { UserService(instance(), instance(), instance()) }
        bind<RoutingV1>() with eagerSingleton { RoutingV1(instance(tag = "upload-dir"), instance(), instance(), instance()) }
    }

    install(Authentication) {
        jwt {
            val jwtService by kodein().instance<JWTTokenService>()
            verifier(jwtService.verifier)
            val userService by kodein().instance<UserService>()

            validate {
                val id = it.payload.getClaim("id").asInt()
                userService.getModelById(id)
            }
        }
    }

    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }
}
