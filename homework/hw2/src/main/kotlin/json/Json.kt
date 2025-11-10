package ru.hsebank.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    ignoreUnknownKeys = true
    allowTrailingComma = true
    explicitNulls = false
    prettyPrint = true
    encodeDefaults = true
    coerceInputValues = true
    isLenient = true
    allowStructuredMapKeys = true
    decodeEnumsCaseInsensitive = true
}