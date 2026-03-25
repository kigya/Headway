package dev.kigya.headway.gateway.presentation.routes

internal sealed interface GatewayHttpRoute {
    val path: String

    data object GraphQL : GatewayHttpRoute {
        override val path: String = "/api/v1/graphql"
    }
}

internal sealed interface GatewayGraphqlOperation {
    val name: String

    data object Health : GatewayGraphqlOperation {
        override val name: String = "_health"
    }

    data object LoginWithGoogle : GatewayGraphqlOperation {
        override val name: String = "loginWithGoogle"
    }

    data object RefreshToken : GatewayGraphqlOperation {
        override val name: String = "refreshToken"
    }

    data object LoginAsGuest : GatewayGraphqlOperation {
        override val name: String = "loginAsGuest"
    }

    data object InviteUser : GatewayGraphqlOperation {
        override val name: String = "inviteUser"
    }

    data object HomeScreen : GatewayGraphqlOperation {
        override val name: String = "homeScreen"
    }
}
