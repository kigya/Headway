package util

import java.io.File
import java.security.MessageDigest
import java.util.Properties

internal object EnvSyncState {

    private const val MARKER_FILE = ".github-env-sync.state"

    fun isUpToDate(
        outputDir: File,
        templatesDir: File,
        owner: String,
        repo: String,
        environment: String,
    ): Boolean {
        val marker = File(outputDir, MARKER_FILE)
        if (!marker.exists()) return false

        val props = Properties()
        marker.inputStream().use { props.load(it) }

        val savedOwner = props.getProperty("owner")
        val savedRepo = props.getProperty("repo")
        val savedEnvironment = props.getProperty("environment")
        val savedTemplatesHash = props.getProperty("templatesHash")

        if (savedOwner != owner) return false
        if (savedRepo != repo) return false
        if (savedEnvironment != environment) return false

        val currentHash = templatesHash(templatesDir)
        if (savedTemplatesHash != currentHash) return false

        val generatedFiles = outputDir.listFiles()
            ?.filter { it.isFile && it.name != MARKER_FILE }
            .orEmpty()

        return generatedFiles.isNotEmpty()
    }

    fun markSuccess(
        outputDir: File,
        templatesDir: File,
        owner: String,
        repo: String,
        environment: String,
    ) {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val marker = File(outputDir, MARKER_FILE)
        val props = Properties()

        props.setProperty("owner", owner)
        props.setProperty("repo", repo)
        props.setProperty("environment", environment)
        props.setProperty("templatesHash", templatesHash(templatesDir))

        marker.outputStream().use { props.store(it, null) }
    }

    private fun templatesHash(dir: File): String {
        val digest = MessageDigest.getInstance("SHA-256")

        val files = dir.listFiles()
            ?.filter { it.isFile && it.name.endsWith(".template") }
            ?.sortedBy { it.name }
            .orEmpty()

        files.forEach { file ->
            digest.update(file.name.toByteArray())
            digest.update(file.readBytes())
        }

        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
