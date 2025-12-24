package ru.hse.store.di

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import kotlinx.io.files.FileSystem
import kotlinx.io.files.SystemFileSystem
import ru.hse.store.api.StoreService
import ru.hse.store.services.StoreServiceImpl
import ru.hse.store.storage.WorkStorage

fun Application.configureDI() {
    dependencies {
        provide<StoreService> { StoreServiceImpl(get(DependencyKey<WorkStorage>())) }
        provide(::WorkStorage)
        provide<FileSystem> { SystemFileSystem }
    }
}