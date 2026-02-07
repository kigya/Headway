package dev.kigya.headway.common.model.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/healthz")
class HealthzResource
