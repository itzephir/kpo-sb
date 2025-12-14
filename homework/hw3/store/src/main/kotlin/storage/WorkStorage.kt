package ru.hse.store.storage

import io.ktor.utils.io.*
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.readByteArray
import org.apache.logging.log4j.kotlin.logger
import java.nio.file.Files
import kotlin.io.path.exists

class WorkStorage(private val fileSystem: FileSystem) {
    private val path = java.nio.file.Paths.get("works")
    private val folder = (if (!path.exists()) {
        Files.createDirectory(path)
    } else path).toAbsolutePath().toString().let {
        Path(it)
    }

    fun save(name: String, bytes: ByteArray): Boolean {
        return try {
            println("Saving $name")
            fileSystem.sink(Path(folder, name)).buffered().use {
                println("Writing $name")
                println("$name: ${bytes.decodeToString()}")
                it.write(bytes)
            }
            println("Saved $name")
            true.also { println("Saved completely $it $name") }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println(e.stackTraceToString())
            e.printStackTrace()
            false
        }
    }

    fun read(name: String): ByteArray? {
        return try {
            logger.info { "Reading $name" }
            fileSystem.source(Path(folder, name)).use {
                println("Read $name")
                it.buffered().readByteArray()
            }.also {
                println("Read completely ${it.decodeToString()}")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.info { e.stackTraceToString() }
            null
        }
    }

    fun delete(name: String): ByteArray? = read(name)?.also {
        fileSystem.delete(Path(name))
    }
}