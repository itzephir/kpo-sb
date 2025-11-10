package ru.hsebank.parser

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

class JsonArgsParser(
    private val json: Json
): ArgsParser {
    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> parse(args: String, kClass: KClass<T>): T {
        val serializer = kClass.serializer()
        return json.decodeFromString(serializer, args)
    }
}