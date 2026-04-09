package base

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

/**
 * Parameters for the web (wasmJs browser) application convention plugin.
 *
 * @property name
 *   Short app name used for webpack `outputFileName` / `outputModuleName` (lowercased in the plugin).
 * @property port
 *   Dev server port for `KotlinWebpackConfig.DevServer`.
 */
public abstract class WebApplicationConventionParams @Inject constructor(
    objects: ObjectFactory,
) {
    @get:Input
    public abstract val name: Property<String>

    @get:Input
    public abstract val port: Property<Int>

    init {
        name.convention("")
        port.convention(8765)
    }
}
