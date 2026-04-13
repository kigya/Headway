package dev.kigya.headway.gateway.kgraphql

import com.apurebase.kgraphql.ContextBuilder
import com.apurebase.kgraphql.GraphQLError
import com.apurebase.kgraphql.GraphqlRequest
import com.apurebase.kgraphql.KGraphQL
import com.apurebase.kgraphql.KtorGraphQLConfiguration
import com.apurebase.kgraphql.context
import com.apurebase.kgraphql.schema.Schema
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.apurebase.kgraphql.schema.dsl.SchemaConfigurationDSL
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.Plugin
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.AttributeKey
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json

internal data class LenientKGraphQL(val schema: Schema) {
    class Configuration : SchemaConfigurationDSL() {
        var playground: Boolean = false

        var endpoint: String = "/graphql"

        internal var contextSetup: (ContextBuilder.(ApplicationCall) -> Unit)? = null
        internal var wrapWith: (Route.(next: Route.() -> Unit) -> Unit)? = null
        internal var errorHandler: ((Throwable) -> Throwable) = { throwable -> throwable }
        internal var schemaBlock: (SchemaBuilder.() -> Unit)? = null

        fun schema(block: SchemaBuilder.() -> Unit) {
            schemaBlock = block
        }

        fun context(block: ContextBuilder.(ApplicationCall) -> Unit) {
            contextSetup = block
        }

        fun wrap(block: Route.(next: Route.() -> Unit) -> Unit) {
            wrapWith = block
        }

        fun errorHandler(block: (throwable: Throwable) -> Throwable) {
            errorHandler = block
        }
    }

    class FeatureInstance(featureKey: String = "LenientKGraphQL") :
        Plugin<Application, Configuration, LenientKGraphQL> {
        override val key = AttributeKey<LenientKGraphQL>(featureKey)

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit,
        ): LenientKGraphQL {
            val config = Configuration().apply(configure)
            val schema = KGraphQL.schema {
                configuration = config
                config.schemaBlock?.let { it(this) }
            }

            val routing: Routing.() -> Unit = {
                val routing: Route.() -> Unit = {
                    route(config.endpoint) {
                        post {
                            val bodyAsText = call.receiveText()
                            val request = graphqlRequestJson.decodeFromString(
                                GraphqlRequest.serializer(),
                                bodyAsText,
                            )
                            val graphQlContext = context {
                                config.contextSetup?.let { it(this, call) }
                            }
                            val result = schema.execute(
                                request = request.query,
                                variables = request.variables.toString(),
                                context = graphQlContext,
                                operationName = request.operationName,
                            )
                            call.respondText(result, contentType = ContentType.Application.Json)
                        }
                        get {
                            val schemaRequested = call.request.queryParameters["schema"] != null
                            if (schemaRequested && config.introspection) {
                                call.respondText(schema.printSchema())
                            } else if (config.playground) {
                                playgroundHtml?.let {
                                    call.respondBytes(it, contentType = ContentType.Text.Html)
                                }
                            }
                        }
                    }
                }

                config.wrapWith?.let { it(this, routing) } ?: routing(this)
            }

            pipeline.pluginOrNull(RoutingRoot)?.apply(routing)
                ?: pipeline.install(RoutingRoot, routing)

            pipeline.intercept(ApplicationCallPipeline.Monitoring) {
                try {
                    coroutineScope {
                        proceed()
                    }
                } catch (exception: Throwable) {
                    val error = config.errorHandler(exception)
                    if (error !is GraphQLError) {
                        throw exception
                    }

                    context.respondText(
                        error.serialize(),
                        ContentType.Application.Json,
                        HttpStatusCode.OK,
                    )
                }
            }
            return LenientKGraphQL(schema)
        }

        companion object {
            private val playgroundHtml: ByteArray? by lazy {
                KtorGraphQLConfiguration::class.java.classLoader.getResource("playground.html")?.readBytes()
            }
        }
    }

    companion object Feature : Plugin<Application, Configuration, LenientKGraphQL> {
        override val key = AttributeKey<LenientKGraphQL>("LenientKGraphQL")

        private val rootFeature = FeatureInstance("LenientKGraphQL")

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit,
        ): LenientKGraphQL = rootFeature.install(pipeline, configure)
    }
}

private val graphqlRequestJson = Json {
    ignoreUnknownKeys = true
}
