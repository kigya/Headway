package dev.kigya.headway.auth.api.model.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/validate-token")
class AuthValidateTokenResource
