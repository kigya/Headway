package base

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * Holds all configurable parameters for the Android Application convention plugin.
 *
 * Your `build.gradle.kts` can call:
 * ```
 * androidApplication {
 *   namespace.set("com.example.myapp")
 *   versionCode.set(42)
 *   versionName.set("2.1.0")
 *   resourceConfigurations.set(listOf("en", "ru"))
 * }
 * ```
 *
 * @property namespace
 *   The Android app’s package namespace (will be written
 *   into your AndroidManifest).  If not set explicitly, defaults
 *   to `"dev.kigya.headway.<projectPath>"`.
 *
 * @property versionCode
 *   Numeric version code for installs (default = `1`).
 *
 * @property versionName
 *   Human‐readable version string (default = `"1.0.0"`).
 *
 * @property resourceConfigurations
 *   List of resource qualifiers to include (e.g. `["ru","en"]`).
 *   Defaults to an empty list.
 *
 * @property headwayGraphqlHttpUrl
 *   Value for manifest placeholder `headwayGraphqlHttpUrl` (GraphQL HTTP URL; default targets the
 *   host loopback from the Android emulator via `10.0.2.2`).
 */
public abstract class AndroidApplicationConventionParams @Inject constructor(
    objects: ObjectFactory,
) {
    @get:Input
    public abstract val namespace: Property<String>

    @get:Input
    public abstract val versionCode: Property<Int>

    @get:Input
    public abstract val versionName: Property<String>

    @get:Input
    public abstract val resourceConfigurations: ListProperty<String>

    @get:Input
    public abstract val headwayGraphqlHttpUrl: Property<String>

    init {
        versionCode.convention(1)
        versionName.convention("1.0.0")
        resourceConfigurations.convention(emptyList())
        headwayGraphqlHttpUrl.convention("https://kigya-headway-dev-gateway.onrender.com/api/v1/graphql")
    }
}
