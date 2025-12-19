package dev.kigya.headway

import com.apurebase.kgraphql.ExecutionException
import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.GraphQLError
import dev.kigya.headway.di.publicGatewayModule
import exception.BadRequestException
import dev.kigya.headway.ext.stringScalarLong
import dev.kigya.headway.ext.stringScalarUUID
import dev.kigya.headway.schema.authSchema
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

internal fun Application.publicGatewayService() {
    install(Koin) {
        modules(publicGatewayModule)
    }
    install(Authentication) {

    }
    install(GraphQL) {
        playground = true
        endpoint = "/api/v1/gql"
        errorHandler { throwable ->
            val originalError = (throwable as ExecutionException).originalError
            when (originalError) {
                is BadRequestException -> GraphQLError(
                    message = originalError.error.message,
                    originalError = originalError.cause,
                    extensions = mapOf(
                        "stacktrace" to originalError.error.stackTrace,
                    )
                )
                else -> GraphQLError(
                    message = throwable.message.toString(),
                    originalError = throwable.cause,
                    extensions = mapOf(
                        "stacktrace" to throwable.stackTrace.map { it.toString() },
                    )
                )
            }
        }
        schema {
            stringScalarUUID()
            stringScalarLong()
            authSchema(client = get())
        }
    }
}
