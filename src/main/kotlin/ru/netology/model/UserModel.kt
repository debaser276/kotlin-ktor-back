package ru.netology.model

import io.ktor.auth.Principal
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users: Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val username: Column<String> = varchar("username", 20).uniqueIndex()
    val password: Column<String> = varchar("password", 100)
}

data class UserModel(
    val id: Int = 0,
    val username: String,
    val password: String
): Principal