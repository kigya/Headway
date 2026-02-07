package dev.kigya.headway.common.model

import dev.kigya.headway.common.extension.KoinHttpClient
import kotlin.reflect.KClass

interface ServiceUrlHolder<T : KoinHttpClient> {
    val baseUrl: String
    val httpClientKClass: KClass<T>
}


inline fun <reified T : KoinHttpClient> serviceUrlHolder(baseUrl: String): ServiceUrlHolder<T> =
    object : ServiceUrlHolder<T> {
        override val baseUrl: String = baseUrl
        override val httpClientKClass: KClass<T> = T::class
    }
