package ru.netology.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.netology.model.Posts
import ru.netology.model.Users
import java.net.URI

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(Users, Posts)
        }
    }

    private fun hikari(): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
//            username = "debaser"
//            password = "gjlyfchtv"
//            jdbcUrl = "jdbc:postgresql://localhost/test"
//            driverClassName = "org.postgresql.Driver"

            val databaseUrl = System.getenv("DATABASE_URL")
            val dbUri = URI(databaseUrl)
            username = dbUri.userInfo.split(":")[0]
            password = dbUri.userInfo.split(":")[1]
            jdbcUrl = "jdbc:postgresql://${dbUri.host}${dbUri.path}"
            driverClassName = "org.postgresql.Driver"
        }
        return HikariDataSource(hikariConfig)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}