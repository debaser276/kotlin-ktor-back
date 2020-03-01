package ru.netology.kodein

import io.ktor.application.Application
import io.ktor.application.log
import org.kodein.di.Kodein
import org.kodein.di.generic.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
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

fun Kodein.MainBuilder.singletons(app: Application) {
    constant(tag = "upload-dir") with (
            app.environment.config.propertyOrNull("static.upload.dir")?.getString() ?:
            throw ConfigurationException("Upload dir is not specified"))
    bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
    bind<JWTTokenService>() with eagerSingleton { JWTTokenService() }
    bind<FileService>() with eagerSingleton { FileService(instance(tag = "upload-dir")) }
    bind<PostRepository>() with singleton { PostRepositoryInMemoryWithMutexImpl(app.log) }
    bind<PostService>() with eagerSingleton { PostService(instance()) }
    bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithMutex() }
    bind<UserService>() with eagerSingleton { UserService(instance(), instance(), instance()) }
    bind<RoutingV1>() with eagerSingleton { RoutingV1(instance(tag = "upload-dir"), instance(), instance(), instance()) }
}