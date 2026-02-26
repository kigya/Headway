package extension

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.internal.catalog.TypeSafeProjectDependencyFactory

public inline operator fun LibrariesForLibs.invoke(block: LibrariesForLibs.() -> Unit) {
    block()
}

public inline operator fun TypeSafeProjectDependencyFactory.invoke(
    block: TypeSafeProjectDependencyFactory.() -> Unit,
) {
    block()
}
