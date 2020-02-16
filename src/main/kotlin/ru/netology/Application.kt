package ru.netology

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.cio.EngineMain
import org.kodein.di.Kodein
import org.kodein.di.generic.*
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import ru.netology.repository.PostRepository
import ru.netology.repository.PostRepositoryInMemoryWithMutexImpl
import ru.netology.route.RoutingV1
import ru.netology.route.v1
import ru.netology.service.FileService
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
        bind<FileService>() with eagerSingleton { FileService(instance(tag = "upload-dir")) }
        bind<PostRepository>() with singleton { PostRepositoryInMemoryWithMutexImpl() }
        bind<RoutingV1>() with eagerSingleton { RoutingV1(instance(tag = "upload-dir"), instance()) }
    }

    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }
}
