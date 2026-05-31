package base

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.JavaExec
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.File

public class DesktopAppConfig {
    public val mainClass: Prop<String> = Prop("MainKt")
    public val packageName: Prop<String> = Prop("App")
    public val packageVersion: Prop<String> = Prop("1.0.0")
    public val iconDir: Prop<File?> = Prop(null)
    public val iconBaseName: Prop<String> = Prop("")
    public val dockName: Prop<String> = Prop("")
    public val bundleId: Prop<String?> = Prop(null)
    public val urlSchemes: Prop<List<String>> = Prop(emptyList())
    public val jlinkModules: Prop<List<String>> = Prop(emptyList())
    public val desktopEnvFile: Prop<File?> = Prop(null)
    public val desktopEnvJvmProperty: Prop<String> = Prop("headway.desktop.env.file")

    private var desktopEnvGradleProperty: String? = null
    private var desktopEnvDefaultName: String = "dev"

    private var _formats: List<TargetFormat> = emptyList()

    public fun formats(vararg formats: TargetFormat) {
        _formats = formats.toList()
    }

    public fun desktopEnvFromSecrets(
        gradlePropertyName: String,
        defaultEnvName: String = "dev",
    ) {
        desktopEnvGradleProperty = gradlePropertyName
        desktopEnvDefaultName = defaultEnvName
    }

    internal fun resolvedFormats(): List<TargetFormat> =
        _formats.ifEmpty {
            listOf(
                when {
                    OperatingSystem.current().isMacOsX -> TargetFormat.Dmg
                    OperatingSystem.current().isWindows -> TargetFormat.Msi
                    else -> TargetFormat.Deb
                },
            )
        }

    internal fun resolveDesktopEnvFile(project: Project): File? {
        desktopEnvFile.get()?.let { return it }
        val gradlePropertyName = desktopEnvGradleProperty ?: return null
        val envDirectoryName = project.providers
            .gradleProperty(gradlePropertyName)
            .orElse(desktopEnvDefaultName)
            .get()
        return project.rootProject.layout.projectDirectory
            .file("secrets/$envDirectoryName/env.desktop")
            .asFile
    }
}

public fun Project.configureDesktopApplication(block: DesktopAppConfig.() -> Unit) {
    val cfg = DesktopAppConfig().apply(block)
    val envFile = cfg.resolveDesktopEnvFile(this)

    pluginManager.withPlugin("org.jetbrains.compose") {
        val composeExt = extensions.getByType(ComposeExtension::class.java)
        val desktopExt = (composeExt as ExtensionAware)
            .extensions
            .getByName("desktop") as DesktopExtension

        desktopExt.application {
            mainClass = cfg.mainClass.get()
            if (envFile != null && envFile.exists()) {
                jvmArgs("-D${cfg.desktopEnvJvmProperty.get()}=${envFile.absolutePath}")
            }
            nativeDistributions {
                packageName = cfg.packageName.get()
                packageVersion = cfg.packageVersion.get()
                targetFormats(*cfg.resolvedFormats().toTypedArray())

                val jlinkModules = cfg.jlinkModules.get()
                if (jlinkModules.isNotEmpty()) {
                    modules(*jlinkModules.toTypedArray())
                }

                val iconDirectory = cfg.iconDir.get()
                val iconBaseName = cfg.iconBaseName.get()
                if (iconDirectory != null && iconBaseName.isNotEmpty()) {
                    windows { iconFile.set(file("$iconDirectory/$iconBaseName.ico")) }
                    linux { iconFile.set(file("$iconDirectory/$iconBaseName.png")) }
                }

                macOS {
                    if (cfg.dockName.get().isNotEmpty()) {
                        dockName = cfg.dockName.get()
                    }
                    if (iconDirectory != null && iconBaseName.isNotEmpty()) {
                        iconFile.set(file("$iconDirectory/$iconBaseName.icns"))
                    }
                    cfg.bundleId.get()?.let { bundleID = it }
                    val urlSchemes = cfg.urlSchemes.get()
                    if (urlSchemes.isNotEmpty()) {
                        infoPlist {
                            extraKeysRawXml = buildMacOsUrlSchemePlistKeys(
                                bundleName = cfg.bundleId.get() ?: cfg.packageName.get(),
                                urlSchemes = urlSchemes,
                            )
                        }
                    }
                }
            }
        }
    }

    configureDesktopJavaExecEnv(envFile)
}

private fun Project.configureDesktopJavaExecEnv(envFile: File?) {
    if (envFile == null) {
        return
    }
    tasks.withType(JavaExec::class.java).configureEach {
        if (name !in desktopJavaExecEnvTaskNames) {
            return@configureEach
        }
        doFirst {
            if (!envFile.exists()) {
                return@doFirst
            }
            envFile.readLines().forEach { line ->
                parseEnvDesktopLine(line)?.let { (key, value) ->
                    environment(key, value)
                }
            }
        }
    }
}

private fun buildMacOsUrlSchemePlistKeys(
    bundleName: String,
    urlSchemes: List<String>,
): String {
    val schemeEntries = urlSchemes.joinToString(separator = "\n") { scheme ->
        "                    <string>$scheme</string>"
    }
    return """
        <key>CFBundleURLTypes</key>
        <array>
            <dict>
                <key>CFBundleURLName</key>
                <string>$bundleName</string>
                <key>CFBundleURLSchemes</key>
                <array>
$schemeEntries
                </array>
            </dict>
        </array>
        """.trimIndent()
}

private fun parseEnvDesktopLine(line: String): Pair<String, String>? {
    val trimmed = line.trim()
    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
        return null
    }
    val equalsIndex = trimmed.indexOf('=')
    if (equalsIndex <= 0) {
        return null
    }
    val key = trimmed.substring(0, equalsIndex).trim()
    val rawValue = trimmed.substring(equalsIndex + 1).trim()
    val value =
        when {
            rawValue.startsWith('"') && rawValue.endsWith('"') && rawValue.length >= 2 ->
                rawValue.substring(1, rawValue.lastIndex)
            rawValue.startsWith('\'') && rawValue.endsWith('\'') && rawValue.length >= 2 ->
                rawValue.substring(1, rawValue.lastIndex)
            else -> rawValue
        }
    return key to value
}

private val desktopJavaExecEnvTaskNames: Set<String> =
    setOf(
        "run",
        "desktopRun",
        "runRelease",
        "runDistributable",
        "runReleaseDistributable",
        "hotRunDesktop",
        "hotRunDesktopAsync",
        "hotDevDesktop",
        "hotDevDesktopAsync",
    )
