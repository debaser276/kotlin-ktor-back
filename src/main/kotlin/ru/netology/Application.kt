package ru.netology

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.routing.Routing
import io.ktor.server.cio.EngineMain
import org.kodein.di.generic.*
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import ru.netology.kodein.singletons
import ru.netology.route.RoutingV1
import ru.netology.service.JWTTokenService
import ru.netology.service.UserService
import ru.netology.statuspages.exceptions

fun main(args : Array<String>) {
    EngineMain.main(args)
}

val hikariConfig = HikariConfig().apply {
    username = "hwxrytwufnyuic"
    password = "58fe0152031067f9a6d577382335dba82ade9592e5316ca65d05d8bbd7a46038"

    jdbcUrl = System.getenv("DATABASE_URL")
    dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
}

val dataSource = HikariDataSource(hikariConfig)

fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    install(StatusPages) {
        exceptions()
    }

    install(KodeinFeature) {
        singletons(this@module)
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
