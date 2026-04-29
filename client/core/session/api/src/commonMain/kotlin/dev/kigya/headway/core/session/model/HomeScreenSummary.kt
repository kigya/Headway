package dev.kigya.headway.core.session.model

import kotlinx.collections.immutable.ImmutableList

data class HomeScreenSummary(
    val userSummary: HomeUserSummary,
    val actions: ImmutableList<HomeScreenAction>,
)
