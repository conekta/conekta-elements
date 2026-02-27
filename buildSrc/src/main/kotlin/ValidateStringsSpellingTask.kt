import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.languagetool.JLanguageTool
import org.languagetool.Languages
import java.io.File

abstract class ValidateStringsSpellingTask : DefaultTask() {
    private val indexedPlaceholderRegex = Regex("""%\d+\$[sdf]""")
    private val simplePlaceholderRegex = Regex("""%[sdf]""")
    private val camelCaseTokenRegex = Regex("""\b[a-z]+(?:[A-Z][a-z0-9]+)+\b""")

    @get:InputFiles
    abstract var stringsFiles: ConfigurableFileTree

    @get:Classpath
    abstract var languageToolClasspath: FileCollection

    @get:Internal
    lateinit var projectDirPath: File

    @TaskAction
    fun validate() {
        var hasErrors = false
        val errorMessages = mutableListOf<String>()

        stringsFiles.forEach { file ->
            val relativePath = file.relativeTo(projectDirPath)
            val locale =
                when {
                    file.path.contains("values-en") -> "en-US"
                    else -> "es" // Default to Spanish
                }

            println("Validating spelling in: $relativePath (locale: $locale)")

            // Get language instance
            val language = Languages.getLanguageForShortCode(locale)
            val langTool = JLanguageTool(language)

            // Parse XML and check each string
            val lines = file.readLines()
            lines.forEachIndexed { lineIndex, line ->
                val stringMatch = Regex("""<string\s+name="([^"]+)"[^>]*>([^<]+)</string>""").find(line)
                if (stringMatch != null) {
                    val stringName = stringMatch.groupValues[1]
                    val rawText = stringMatch.groupValues[2]
                    val hasFormatPlaceholders =
                        indexedPlaceholderRegex.containsMatchIn(rawText) || simplePlaceholderRegex.containsMatchIn(rawText)
                    val text =
                        rawText
                            .replace("&amp;", "&")
                            .replace("&lt;", "<")
                            .replace("&gt;", ">")
                            .replace("&quot;", "\"")
                            .replace("&#39;", "'")
                            .trim()

                    // Skip validation for certain strings
                    if (text.matches(Regex("^[A-Z/]+$")) ||
                        // All caps like "MM/YY" or "CVV"
                        text.contains("Conekta") ||
                        // Brand names
                        text.contains("Mastercard") ||
                        text.contains("American Express") ||
                        text.contains("Visa") ||
                        // Strings with format placeholders are valid and noisy for grammar rules
                        hasFormatPlaceholders ||
                        // Fragments intended to be concatenated with other strings
                        stringName.endsWith("_prefix") ||
                        stringName.endsWith("_suffix") ||
                        stringName.endsWith("_more_link") ||
                        // Provider lists contain many proper nouns and brand names
                        stringName.endsWith("_provider_list_more") ||
                        // Proper nouns/brands frequently flagged by generic dictionary
                        text.contains("BBVA") ||
                        text.contains("CLABE") ||
                        text.contains("Eleczion") ||
                        text.contains("FarmaTodo") ||
                        text.contains("Practicaja") ||
                        text.contains("Waldos") ||
                        text.contains("Woolworth") ||
                        text.contains("Yepas") ||
                        text.contains("Alsúper") ||
                        // Validation messages can include technical identifiers (camelCase)
                        camelCaseTokenRegex.containsMatchIn(text)
                    ) {
                        return@forEachIndexed
                    }

                    // Check spelling
                    val matches = langTool.check(text)

                    if (matches.isNotEmpty()) {
                        matches.forEach { match ->
                            hasErrors = true
                            errorMessages.add("\n⚠️  File: $relativePath:${lineIndex + 1}")
                            errorMessages.add("   String: $stringName")
                            errorMessages.add("   Text: \"$text\"")
                            errorMessages.add("   Issue: ${match.shortMessage.ifEmpty { match.message }}")
                            if (match.suggestedReplacements.isNotEmpty()) {
                                errorMessages.add(
                                    "   Suggestions: ${
                                        match.suggestedReplacements.take(3).joinToString(", ")
                                    }",
                                )
                            }
                        }
                    }
                }
            }
        }

        if (hasErrors) {
            errorMessages.forEach { println(it) }
            throw GradleException("\n⚠️  Spelling/grammar validation failed! Please fix the issues above.")
        } else {
            println("\n✓ All string resources have correct spelling and grammar!")
        }
    }
}
