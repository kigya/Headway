pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/kigya/GithubEnvSync-Plugin")
            credentials {
                fun decryptGithubEnvSyncPackageCredentialToken(
                    hexInput: String,
                    key: String,
                ): String =
                    hexInput.chunked(2).mapIndexed { index, hexChar ->
                        val originalChar = hexChar.toInt(16) xor key[index % key.length].code
                        originalChar.toChar()
                    }.joinToString("")

                val propertyUsername = providers.gradleProperty("github.env.sync.username").getOrNull()
                val gprUsername = providers.environmentVariable("GPR_USER").getOrNull()
                username = propertyUsername ?: gprUsername

                val encryptedToken = providers.gradleProperty("github.env.sync.token").getOrNull()
                val decryptionKey = providers.gradleProperty("github.env.sync.token.key").getOrNull()
                val gprPat = providers.environmentVariable("GPR_KEY").getOrNull()

                password = when {
                    encryptedToken != null && decryptionKey != null ->
                        decryptGithubEnvSyncPackageCredentialToken(encryptedToken, decryptionKey)

                    gprPat != null -> gprPat
                    else -> null
                }
            }
        }
    }
    includeBuild("build-logic")
}
