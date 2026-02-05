import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ValidateStringsOrderTask : DefaultTask() {
    @get:InputFiles
    abstract var stringsFiles: ConfigurableFileTree

    @get:Internal
    lateinit var projectDirPath: File

    @TaskAction
    fun validate() {
        var hasErrors = false
        val errorMessages = mutableListOf<String>()

        stringsFiles.forEach { file ->
            val relativePath = file.relativeTo(projectDirPath)
            println("Validating: $relativePath")

            val stringNames = mutableListOf<String>()
            val lines = file.readLines()

            lines.forEach { line ->
                // Extract string name attributes
                val stringMatch = Regex("""<string\s+name="([^"]+)"""").find(line)
                if (stringMatch != null) {
                    stringNames.add(stringMatch.groupValues[1])
                }
            }

            // Check if sorted
            val sortedNames = stringNames.sorted()
            if (stringNames != sortedNames) {
                hasErrors = true
                errorMessages.add("\n❌ File: $relativePath")
                errorMessages.add("   Strings are NOT in alphabetical order!")
                errorMessages.add("   Expected order:")

                // Find and report misplaced strings
                stringNames.forEachIndexed { index, name ->
                    if (index < sortedNames.size && name != sortedNames[index]) {
                        errorMessages.add("   Position ${index + 1}: Found '$name', expected '${sortedNames[index]}'")
                    }
                }
            } else {
                println("✓ Strings in $relativePath are correctly sorted")
            }
        }

        if (hasErrors) {
            errorMessages.forEach { println(it) }
            throw GradleException("\n⚠️  String validation failed! Please sort the strings alphabetically.")
        } else {
            println("\n✓ All string resources are in alphabetical order!")
        }
    }
}
