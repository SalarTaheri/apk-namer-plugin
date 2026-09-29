import org.gradle.api.Project
import java.util.concurrent.TimeUnit

object GitHelper {
    fun getShortGitSha(project: Project): String? {
        return try {
            val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
                .directory(project.rootDir)
                .redirectErrorStream(true)
                .start()
            val finished = process.waitFor(3, TimeUnit.SECONDS)
            if (finished && process.exitValue() == 0) {
                process.inputStream.bufferedReader().readText().trim().takeIf { it.isNotEmpty() }
            } else {
                process.destroyForcibly()
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
