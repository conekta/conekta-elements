package io.conekta.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.font.FontFamily
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class ConektaThemeTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun localConektaFontFamilyDefaultIsFontFamilyDefault() =
        runComposeUiTest {
            var defaultFont: FontFamily? = null
            setContent {
                defaultFont = LocalConektaFontFamily.current
            }
            assertEquals(FontFamily.Default, defaultFont)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun conektaThemeRendersContent() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    androidx.compose.material3.Text("Theme test")
                }
            }
            onNodeWithText("Theme test").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun conektaThemeAppliesColorScheme() =
        runComposeUiTest {
            var primaryColor: androidx.compose.ui.graphics.Color? = null
            setContent {
                ConektaTheme {
                    primaryColor = MaterialTheme.colorScheme.primary
                    androidx.compose.material3.Text("Colors")
                }
            }
            onNodeWithText("Colors").assertIsDisplayed()
            assertEquals(ConektaColors.CoreIndigo, primaryColor)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun conektaThemeAppliesOnPrimaryColor() =
        runComposeUiTest {
            var onPrimaryColor: androidx.compose.ui.graphics.Color? = null
            setContent {
                ConektaTheme {
                    onPrimaryColor = MaterialTheme.colorScheme.onPrimary
                    androidx.compose.material3.Text("OnPrimary")
                }
            }
            onNodeWithText("OnPrimary").assertIsDisplayed()
            assertEquals(ConektaColors.Pearl100, onPrimaryColor)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun conektaThemeAppliesSurfaceColor() =
        runComposeUiTest {
            var surfaceColor: androidx.compose.ui.graphics.Color? = null
            setContent {
                ConektaTheme {
                    surfaceColor = MaterialTheme.colorScheme.surface
                    androidx.compose.material3.Text("Surface")
                }
            }
            onNodeWithText("Surface").assertIsDisplayed()
            assertEquals(ConektaColors.Surface, surfaceColor)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun conektaThemeAppliesOutlineColor() =
        runComposeUiTest {
            var outlineColor: androidx.compose.ui.graphics.Color? = null
            setContent {
                ConektaTheme {
                    outlineColor = MaterialTheme.colorScheme.outline
                    androidx.compose.material3.Text("Outline")
                }
            }
            onNodeWithText("Outline").assertIsDisplayed()
            assertEquals(ConektaColors.Neutral5, outlineColor)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun conektaThemeProvidesFontFamily() =
        runComposeUiTest {
            var fontFamily: FontFamily? = null
            setContent {
                ConektaTheme {
                    fontFamily = LocalConektaFontFamily.current
                    androidx.compose.material3.Text("Font")
                }
            }
            onNodeWithText("Font").assertIsDisplayed()
            assertNotNull(fontFamily)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun conektaThemeAppliesTypography() =
        runComposeUiTest {
            var typographyFontFamily: FontFamily? = null
            setContent {
                ConektaTheme {
                    typographyFontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    androidx.compose.material3.Text("Typography")
                }
            }
            onNodeWithText("Typography").assertIsDisplayed()
            assertNotNull(typographyFontFamily)
        }
}
