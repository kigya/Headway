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

                fun nonBlankEnvironmentVariable(name: String): String? =
                    System.getenv(name)?.takeIf { it.isNotEmpty() }

                val propertyUsername = providers.gradleProperty("github.env.sync.username").getOrNull()
                val gprUsername = nonBlankEnvironmentVariable("GPR_USER")
                val githubActor = nonBlankEnvironmentVariable("GITHUB_ACTOR")

                val encryptedToken = providers.gradleProperty("github.env.sync.token").getOrNull()
                val decryptionKey = providers.gradleProperty("github.env.sync.token.key").getOrNull()
                val gprPat = nonBlankEnvironmentVariable("GPR_KEY")

                val decryptedPassword =
                    if (encryptedToken != null && decryptionKey != null) {
                        decryptGithubEnvSyncPackageCredentialToken(encryptedToken, decryptionKey)
                    } else {
                        null
                    }

                val effectivePassword = decryptedPassword ?: gprPat

                username = propertyUsername
                    ?: gprUsername
                    ?: if (gprPat != null) {
                        githubActor
                    } else {
                        null
                    }

                password = effectivePassword
            }
        }
    }
    includeBuild("build-logic")
}
