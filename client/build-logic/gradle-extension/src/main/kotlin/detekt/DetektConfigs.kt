package detekt

public object DetektConfigs {
    private const val DETEKT_CONFIG_PATH: String = "../config/detekt"

    public const val CLIENT: String = "$DETEKT_CONFIG_PATH/client.yml"
    public const val COMPOSE: String = "$DETEKT_CONFIG_PATH/compose.yml"
}
