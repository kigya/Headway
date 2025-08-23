package detekt

public object DetektConfigs {
    private const val DETEKT_CONFIG_PATH: String = "config/detekt"

    public const val MAIN: String = "$DETEKT_CONFIG_PATH/main.yml"
    public const val COMPOSE: String = "$DETEKT_CONFIG_PATH/compose.yml"
}
