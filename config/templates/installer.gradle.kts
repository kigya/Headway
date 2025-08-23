import org.gradle.internal.os.OperatingSystem

tasks.register<Exec>("installLiveTemplates") {
    group = "templates"
    description = "Install IDE Live Templates into the latest Android Studio config"

    val script = rootProject.file("config/templates/install-live-templates.sh")

    val templatesDir = (project.findProperty("templatesDir") as String?)
        ?: rootProject.file("config/templates").absolutePath

    doFirst {
        if (!OperatingSystem.current().isWindows) {
            script.setExecutable(true)
        }
    }

    if (OperatingSystem.current().isWindows) {
        commandLine("bash", script.absolutePath, templatesDir)
    } else {
        commandLine(script.absolutePath, templatesDir)
    }
}
