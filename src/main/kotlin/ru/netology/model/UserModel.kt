package ru.netology.model

import io.ktor.auth.Principal

data class UserModel(
    val id: Int = 1,
    val username: String,
    val password: String
): Principal