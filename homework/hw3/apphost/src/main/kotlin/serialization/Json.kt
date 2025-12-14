package ru.hse.apphost.serialization

import kotlinx.serialization.json.Json

val json = Json {
    encodeDefaults = true
    coerceInputValues = true
    explicitNulls = false
}