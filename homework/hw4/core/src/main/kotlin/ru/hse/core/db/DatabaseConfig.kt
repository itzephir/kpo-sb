package ru.hse.core.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.Table

fun Application.configureDatabase(vararg tables: Table) {
    val dataSource = hikari()
    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(*tables)
    }
}

private fun Application.hikari(): HikariDataSource {
    val config = HikariConfig().apply {
        driverClassName = environment.config.property("postgres.driver").getString()
        jdbcUrl = environment.config.property("postgres.url").getString()
        username = environment.config.property("postgres.username").getString()
        password = environment.config.property("postgres.password").getString()
        maximumPoolSize = environment.config.property("postgres.maximumPoolSize").getString().toInt()
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(config)
}