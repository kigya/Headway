package dev.kigya.headway.core.session.model

data class HomeScreenAction(
    val semanticType: HomeActionSemanticType,
    val title: String,
    val style: HomeActionVisualStyle,
    val iconUrl: String,
)
