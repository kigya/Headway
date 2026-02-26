package base

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

public abstract class WebApplicationConventionParams @Inject constructor(
    objects: ObjectFactory,
) {
    @get:Input
    public abstract val name: Property<String>

    init {
        name.convention("")
    }
}
