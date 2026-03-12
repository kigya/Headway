package gradle

import java.io.File
import java.util.Properties

internal object ProjectGradleProperties {

    private fun propertiesFile(): File {
        val projectDir = File(System.getProperty("user.dir"))

        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }

        val file = File(projectDir, "gradle.properties")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    fun read(key: String): String? {
        val props = Properties()
        val file = propertiesFile()
        file.inputStream().use { props.load(it) }
        return props.getProperty(key)
    }

    fun write(key: String, value: String) {
        val props = Properties()
        val file = propertiesFile()
        file.inputStream().use { props.load(it) }
        props.setProperty(key, value)
        file.outputStream().use { props.store(it, null) }
    }

    fun remove(key: String) {
        val props = Properties()
        val file = propertiesFile()
        file.inputStream().use { props.load(it) }
        props.remove(key)
        file.outputStream().use { props.store(it, null) }
    }
}
