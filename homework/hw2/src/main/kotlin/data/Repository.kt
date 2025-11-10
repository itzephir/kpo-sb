package ru.hsebank.data

import kotlinx.io.Sink
import kotlinx.io.Source

interface Repository<T> {
    suspend fun read(source: Source): T
    suspend fun write(sink: Sink, data: T)

    val format: String
}
