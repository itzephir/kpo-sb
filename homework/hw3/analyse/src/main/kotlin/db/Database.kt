package ru.hse.analyse.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.configureDatabase() {
    Database.connect(hikari())

    transaction {
        SchemaUtils.create(Reports)
    }
}

private fun Application.hikari(): HikariDataSource {
    val config = HikariConfig().apply {
        driverClassName = environment.config.property("postgres.driver").getString()
        jdbcUrl = environment.config.property("postgres.url").getString()
        username = environment.config.property("postgres.username").getString()
        password = environment.config.property("postgres.password").getString()
        maximumPoolSize = environment.config.property("postgres.maximumPoolSize").getString().toInt()
        isAutoCommit = environment.config.property("postgres.autoCommit").getString().toBoolean()
        transactionIsolation = environment.config.property("postgres.transactionIsolation").getString()
        validate()
    }
    return HikariDataSource(config)
}