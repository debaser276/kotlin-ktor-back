package ru.netology.route

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import ru.netology.dto.PostResponseDto
import ru.netology.repository.PostRepository

fun Routing.v1() {
    route("/api/v1/posts"){
        val repo by kodein().instance<PostRepository>()
        get {
            val response = repo.getAll().map { PostResponseDto.fromModel(it) }
            call.respond(response)
        }
    }
}