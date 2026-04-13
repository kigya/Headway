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

                username = providers.gradleProperty("github.env.sync.username").getOrNull()
                password = providers
                    .gradleProperty("github.env.sync.token")
                    .getOrNull()
                    ?.let { encryptedHex ->
                        val key = providers
                            .gradleProperty("github.env.sync.token.key")
                            .getOrNull()
                        requireNotNull(key) {
                            "Github token decryption key (github.env.sync.token.key) is required"
                        }

                        decryptGithubEnvSyncPackageCredentialToken(encryptedHex, key)
                    }
            }
        }
    }
    includeBuild("build-logic")
}
