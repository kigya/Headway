package detekt

object DetektConfigs {
    private const val DETEKT_CONFIG_PATH = "config/detekt"

    const val MAIN = "$DETEKT_CONFIG_PATH/main.yml"
    const val COMPOSE = "$DETEKT_CONFIG_PATH/compose.yml"
}
