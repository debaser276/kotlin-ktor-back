package ru.netology.statuspages

import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import ru.netology.dto.ErrorResponseDto
import ru.netology.exception.*

fun StatusPages.Configuration.exceptions() {
    exception<DatabaseException> {
        call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Database exception"))
    }
    exception<LoginAlreadyExistsException> {
        call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Login already exists"))
    }
    exception<NoPostsException> {
        call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("There are no post yet"))
    }
    exception<PushTokenNotFoundException> {
        call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Push token not found"))
    }
    exception<AlreadyRepostedException> {
        call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("This post has already been reposted"))
    }
    exception<InvalidPasswordException> { e ->
        call.respond(HttpStatusCode.Unauthorized, ErrorResponseDto("Wrong password"))
        throw e
    }
    exception<UserNotFoundException> { e ->
        call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("User not found"))
        throw e
    }
    exception<ForbiddenException>() { e ->
        call.respond(HttpStatusCode.Forbidden, ErrorResponseDto("Access denied!"))
        throw e
    }
    exception<PostNotFoundException> { e ->
        call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Post with provided id not found"))
        throw e
    }
    exception<AlreadyLikedException> { e ->
        call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("This post has already been liked"))
        throw e
    }
    exception<NotLikedYetException> { e ->
        call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("This post haven't been liked yet"))
        throw e
    }
    exception<NotFoundException> { e ->
        call.respond(HttpStatusCode.NotFound)
        throw e
    }
    exception<ParameterConversionException> { e ->
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