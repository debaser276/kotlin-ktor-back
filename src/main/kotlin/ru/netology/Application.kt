package ru.netology

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.routing.Routing
import io.ktor.server.cio.EngineMain
import org.kodein.di.ktor.KodeinFeature
import ru.netology.route.v1


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

    install(StatusPages)

    install(KodeinFeature) {

    }

    install(Routing) {
        v1()
    }
}