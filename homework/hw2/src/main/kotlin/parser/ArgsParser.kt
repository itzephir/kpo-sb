package ru.hsebank.parser

import kotlin.reflect.KClass

interface ArgsParser {
    fun <T : Any> parse(args: String, kClass: KClass<T>): T

    companion object {
        inline fun <reified T : Any> ArgsParser.parse(args: String): T = parse(args, T::class)
    }
}
