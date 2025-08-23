import org.apache.tools.ant.taskdefs.condition.Os

val gitHooksPath = "config/git/hooks"

tasks.register<Exec>("installGitHooks") {
    description = "Installs the pre-commit git hooks from $gitHooksPath"
    group = "git hooks"
    workingDir = rootDir

    commandLine = listOf("git", "config", "core.hooksPath", gitHooksPath)

    dependsOn("makeGitHooksExecutable")

    doLast {
        logger.info("Git hook installed successfully.")
    }
}

tasks.register<Exec>("makeGitHooksExecutable") {
    description = "Make executable the pre-commit git hooks from $gitHooksPath"
    group = "git hooks"

    workingDir = rootDir
    commandLine = listOf("chmod", "-R", "+x", gitHooksPath)

    val isWindows = Os.isFamily(Os.FAMILY_WINDOWS)
    if (isWindows) {
        logger.info("No need to make git hooks executable on Windows.")
    }
    onlyIf { !isWindows }

    doLast {
        logger.info("Git hooks made executable.")
    }
}
