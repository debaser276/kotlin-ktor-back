package ru.netology.kodein

import io.ktor.application.Application
import io.ktor.application.log
import org.kodein.di.Kodein
import org.kodein.di.generic.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import ru.netology.exception.ConfigurationException
import ru.netology.repository.PostRepository
import ru.netology.repository.PostRepositoryDatabase
import ru.netology.repository.UserRepository
import ru.netology.repository.UserRepositoryDatabase
import ru.netology.route.RoutingV1
import ru.netology.service.*

fun Kodein.MainBuilder.singletons(app: Application) {
    constant(tag = "result-size") with (
            app.environment.config.propertyOrNull("secondapp.api.result-size")?.getString()?.toInt() ?:
                throw ConfigurationException("Result-size is not specified"))
    constant(tag = "upload-dir") with (
            app.environment.config.propertyOrNull("secondapp.upload.dir")?.getString() ?:
                throw ConfigurationException("Upload dir is not specified"))
    constant(tag = "jwt-secret") with (
            app.environment.config.propertyOrNull("secondapp.jwt.secret")?.getString() ?:
                throw ConfigurationException("JWT secret is not specified"))
    constant(tag = "fcm-password") with (
            app.environment.config.propertyOrNull("secondapp.fcm.password")?.getString() ?:
                throw ConfigurationException("FCM password is not specified"))
    constant(tag = "fcm-salt") with (
            app.environment.config.propertyOrNull("secondapp.fcm.salt")?.getString() ?:
                throw ConfigurationException("FCM salt is not specified"))
    constant(tag = "fcm-db-url") with (
            app.environment.config.propertyOrNull("secondapp.fcm.db-url")?.getString() ?:
                throw ConfigurationException("FCM db url is not specified"))
    constant(tag = "fcm-path") with (
            app.environment.config.propertyOrNull("secondapp.fcm.path")?.getString() ?:
                throw ConfigurationException("FCM JSON path is not specified"))
    bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
    bind<JWTTokenService>() with eagerSingleton { JWTTokenService(instance(tag = "jwt-secret")) }
    bind<FileService>() with eagerSingleton { FileService(instance(tag = "upload-dir")) }
    bind<PostRepository>() with singleton { PostRepositoryDatabase() }
    bind<PostService>() with eagerSingleton { PostService(instance(), instance(tag = "result-size")) }
    bind<UserRepository>() with eagerSingleton { UserRepositoryDatabase() }
    bind<UserService>() with eagerSingleton { UserService(instance(), instance(), instance()) }
    bind<RoutingV1>() with eagerSingleton {
        RoutingV1(instance(tag = "upload-dir"),
            instance(),
            instance(),
            instance(),
            instance(),
            app.log) }
    bind<FCMService>() with eagerSingleton {
        FCMService(
            instance(tag = "fcm-db-url"),
            instance(tag = "fcm-password"),
            instance(tag = "fcm-salt"),
            instance(tag = "fcm-path")
        )
    }
}