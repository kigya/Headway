package dev.kigya.headway.navigation.api.type

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified TEnum : Enum<TEnum>> enumNavTypeEntry(
    crossinline valueOf: (String) -> TEnum,
): Pair<KType, NavType<TEnum>> =
    typeOf<TEnum>() to object : NavType<TEnum>(isNullableAllowed = false) {

        override fun get(
            bundle: SavedState,
            key: String,
        ): TEnum? = bundle.read { getString(key) }.let(::parseValue)

        override fun parseValue(value: String): TEnum = valueOf(value)

        override fun put(
            bundle: SavedState,
            key: String,
            value: TEnum,
        ) {
            bundle.write {
                putString(key, value.name)
            }
        }
    }

inline fun <reified TEnum : Enum<TEnum>> nullableEnumNavType(
    crossinline valueOf: (String) -> TEnum,
): NavType<TEnum?> = object : NavType<TEnum?>(isNullableAllowed = true) {

    override fun get(
        bundle: SavedState,
        key: String,
    ): TEnum? = bundle.read { getString(key) }.let {
        if (it.isBlank()) null else valueOf(it)
    }

    override fun parseValue(value: String): TEnum? = if (value.isBlank()) null else valueOf(value)

    override fun put(
        bundle: SavedState,
        key: String,
        value: TEnum?,
    ) {
        bundle.write { putString(key, value?.name.orEmpty()) }
    }
}
