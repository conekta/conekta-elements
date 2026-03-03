import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

/**
 * Copies Gradle-processed Compose resources (.cvr files) into the committed
 * Sources/ComposeResources/ directory so that SPM consumers get up-to-date resources.
 *
 * Register in compose/build.gradle.kts with a dependsOn on the resource-processing task
 * (e.g. iosArm64ProcessResources) so the input directory is populated before this runs.
 */
abstract class SyncComposeResourcesToSPMTask : DefaultTask() {

    /** Built resources from Gradle (processedResources/iosArm64/main/composeResources). */
    @get:InputDirectory
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val builtResourcesDir: DirectoryProperty

    /** SPM sources directory (Sources/ComposeResources/compose-resources/composeResources). */
    @get:OutputDirectory
    abstract val spmResourcesDir: DirectoryProperty

    @TaskAction
    fun sync() {
        val src = builtResourcesDir.get().asFile
        val dest = spmResourcesDir.get().asFile

        if (dest.exists()) {
            dest.deleteRecursively()
        }
        dest.mkdirs()

        src.copyRecursively(dest)

        val count = dest.walkTopDown().filter { it.isFile }.count()
        println("Synced $count files from ${src.path} to ${dest.path}")
    }
}
