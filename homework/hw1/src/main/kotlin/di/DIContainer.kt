package com.itzephir.kpo.homework1.di

import arrow.core.Either
import arrow.core.raise.either
import kotlin.reflect.KClass

class DIContainer {
    private val definitions = mutableMapOf<String, DIDefinition<Any>>()

    private val classKeys = mutableMapOf<String, String>()

    @PublishedApi
    internal fun register(key: String, definition: DIDefinition<Any>): Either<DIDefinitionAlreadyDefinedError, Unit> =
        either {
            if (key in definitions) {
                raise(DIDefinitionAlreadyDefinedError(key))
            }
            definitions[key] = definition
        }

    @PublishedApi
    internal fun get(key: String): Either<DIDefinitionNotFoundError, Any> = either {
        if (key !in definitions) raise(DIDefinitionNotFoundError(key))
        definitions.getValue(key).get()
    }

    companion object {

        fun <T : Any> DIContainer.singleton(
            kClass: KClass<T>,
            instance: T,
        ): Either<DIDefinitionAlreadyDefinedError, Unit> {
            val key = kClass.qualifiedName + "singleton"
            classKeys[kClass.qualifiedName ?: ""] = key
            return register(key, DISingletonDefinition(instance as Any))
        }

        fun <T : Any> DIContainer.factory(
            kClass: KClass<*>,
            factory: () -> T,
        ): Either<DIDefinitionAlreadyDefinedError, Unit> {
            val factory = { factory() as Any }
            val key = kClass.qualifiedName + "factory"
            classKeys[kClass.qualifiedName ?: ""] = key
            return register(key, DIFactoryDefinition(factory))
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> DIContainer.get(kClass: KClass<T>): Either<DIDefinitionNotFoundError, T> = either {
            val key =
                classKeys[kClass.qualifiedName ?: ""] ?: raise(DIDefinitionNotFoundError(kClass.qualifiedName ?: ""))
            get(key).map { it as T }.bind()
        }

        inline fun <reified T : Any> DIContainer.singleton(instance: T): Either<DIDefinitionAlreadyDefinedError, Unit> =
            singleton(T::class, instance)

        inline fun <reified T : Any> DIContainer.factory(noinline factory: () -> T): Either<DIDefinitionAlreadyDefinedError, Unit> =
            factory(T::class, factory)

        inline fun <reified T : Any> DIContainer.get(): Either<DIDefinitionNotFoundError, T> =
            get(T::class)

        inline fun <reified T> DIContainer.inject(): Lazy<Either<DIDefinitionNotFoundError, T>> = lazy { get() }
    }
}