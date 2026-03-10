import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Verifies that Sources/ComposeResources/composeResources/ is in sync with the Compose
 * Multiplatform build output. Fails with a clear message if any file is missing or has
 * different content, so a release is never shipped with stale resources.
 *
 * Register in compose/build.gradle.kts and wire it to the check task or to a CI step.
 */
abstract class VerifyComposeResourcesSyncTask : DefaultTask() {

    /** Built resources from Gradle (processedResources/iosArm64/main/composeResources). */
    @get:InputDirectory
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val builtResourcesDir: DirectoryProperty

    /** SPM sources directory (Sources/ComposeResources/composeResources). */
    @get:Internal
    abstract val spmResourcesDir: DirectoryProperty

    @TaskAction
    fun verify() {
        val builtDir = builtResourcesDir.get().asFile
        val spmDir = spmResourcesDir.get().asFile

        val builtFiles = builtDir.relativeFileMap()
        val spmFiles = spmDir.relativeFileMap()

        val missingInSpm = builtFiles.keys - spmFiles.keys
        val extraInSpm = spmFiles.keys - builtFiles.keys
        val outdated = builtFiles.keys.intersect(spmFiles.keys)
            .filter { path -> !builtFiles[path]!!.readBytes().contentEquals(spmFiles[path]!!.readBytes()) }

        if (missingInSpm.isEmpty() && extraInSpm.isEmpty() && outdated.isEmpty()) {
            println("✓ Sources/ComposeResources/composeResources/ is up to date")
            return
        }

        val report = buildString {
            appendLine("Sources/ComposeResources/composeResources/ is out of sync with the build output.")
            appendLine("Run:  ./gradlew :compose:assembleComposeKitReleaseXCFramework")
            appendLine("      (syncComposeResourcesToSPM runs automatically afterwards)")
            if (missingInSpm.isNotEmpty()) {
                appendLine("\nMissing from Sources/ComposeResources:")
                missingInSpm.sorted().forEach { appendLine("  - $it") }
            }
            if (extraInSpm.isNotEmpty()) {
                appendLine("\nExtra files in Sources/ComposeResources (not in build output):")
                extraInSpm.sorted().forEach { appendLine("  + $it") }
            }
            if (outdated.isNotEmpty()) {
                appendLine("\nFiles with different content:")
                outdated.sorted().forEach { appendLine("  ~ $it") }
            }
        }

        error(report)
    }

    private fun File.relativeFileMap(): Map<String, File> =
        if (!exists()) emptyMap()
        else walkTopDown()
            .filter { it.isFile }
            .associate { it.relativeTo(this).path to it }
}
