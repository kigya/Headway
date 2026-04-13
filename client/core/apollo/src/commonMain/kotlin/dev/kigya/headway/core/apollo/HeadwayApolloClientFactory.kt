package dev.kigya.headway.core.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import okio.Buffer

fun createHeadwayApolloClient(
    serverUrl: String,
    accessTokenProvider: () -> String?,
): ApolloClient {
    val builder = ApolloClient.Builder()
        .serverUrl(serverUrl)
        .addHttpInterceptor(
            HeadwayGraphQlStripNestedDataExtensionsHttpInterceptor(),
        )
        .addHttpInterceptor(
            BearerAuthorizationHttpInterceptor(accessTokenProvider),
        )
    return builder.build()
}

private class HeadwayGraphQlStripNestedDataExtensionsHttpInterceptor : HttpInterceptor {

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain,
    ): HttpResponse {
        val response = chain.proceed(request)
        val source = response.body ?: return response
        val text = try {
            source.readUtf8()
        } catch (_: Throwable) {
            return response
        } finally {
            runCatching { source.close() }
        }
        val rewritten = rewriteGraphQlJsonWithoutExtensionsUnderData(text)
        return HttpResponse.Builder(statusCode = response.statusCode)
            .apply {
                addHeaders(response.headers)
                body(Buffer().writeUtf8(rewritten))
            }
            .build()
    }
}

private fun rewriteGraphQlJsonWithoutExtensionsUnderData(text: String): String {
    val root = runCatching {
        headwayGraphQlHttpJson.parseToJsonElement(text).jsonObject
    }.getOrNull() ?: return text
    val dataElement = root[HEADWAY_GRAPHQL_JSON_KEY_DATA] ?: return text
    val dataObject = dataElement.asJsonObjectOrNull() ?: return text
    var changed = false
    val newDataObject = buildJsonObject {
        dataObject.forEach { (fieldKey, fieldElement) ->
            val fieldJsonObject = fieldElement.asJsonObjectOrNull()
            if (fieldJsonObject != null && fieldJsonObject.containsKey(HEADWAY_GRAPHQL_JSON_KEY_EXTENSIONS)) {
                changed = true
                put(
                    fieldKey,
                    buildJsonObject {
                        fieldJsonObject.forEach { (nestedKey, nestedValue) ->
                            if (nestedKey != HEADWAY_GRAPHQL_JSON_KEY_EXTENSIONS) {
                                put(nestedKey, nestedValue)
                            }
                        }
                    },
                )
            } else {
                put(fieldKey, fieldElement)
            }
        }
    }
    if (!changed) {
        return text
    }
    val outRoot = buildJsonObject {
        root.forEach { (topKey, topValue) ->
            if (topKey == HEADWAY_GRAPHQL_JSON_KEY_DATA) {
                put(topKey, newDataObject)
            } else {
                put(topKey, topValue)
            }
        }
    }
    return headwayGraphQlHttpJson.encodeToString(outRoot)
}

private fun JsonElement.asJsonObjectOrNull(): JsonObject? = this as? JsonObject

private val headwayGraphQlHttpJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private class BearerAuthorizationHttpInterceptor(
    private val accessTokenProvider: () -> String?,
) : HttpInterceptor {

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain,
    ): HttpResponse {
        val token = accessTokenProvider()
        val authorized = if (token.isNullOrBlank()) {
            request
        } else {
            request.newBuilder().addHeader("Authorization", "Bearer $token").build()
        }
        return chain.proceed(authorized)
    }
}

private const val HEADWAY_GRAPHQL_JSON_KEY_DATA: String = "data"

private const val HEADWAY_GRAPHQL_JSON_KEY_EXTENSIONS: String = "extensions"
