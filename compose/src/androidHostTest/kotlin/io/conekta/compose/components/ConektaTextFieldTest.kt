package io.conekta.compose.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.conekta.compose.theme.ConektaTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConektaTextFieldTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun textFieldDisplaysLabel() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaTextField(
                        value = TextFieldValue(""),
                        onValueChange = {},
                        label = "Nombre en la tarjeta",
                        placeholder = "Nombre",
                    )
                }
            }
            onNodeWithText("Nombre en la tarjeta").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun textFieldShowsPlaceholder() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaTextField(
                        value = TextFieldValue(""),
                        onValueChange = {},
                        label = "Label",
                        placeholder = "Placeholder text",
                    )
                }
            }
            onNodeWithText("Placeholder text").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun textFieldShowsErrorMessage() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaTextField(
                        value = TextFieldValue(""),
                        onValueChange = {},
                        label = "Label",
                        placeholder = "Placeholder",
                        isError = true,
                        errorMessage = "Este campo es obligatorio",
                    )
                }
            }
            onNodeWithText("Este campo es obligatorio").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun textFieldDoesNotShowErrorWhenNotError() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaTextField(
                        value = TextFieldValue(""),
                        onValueChange = {},
                        label = "Label",
                        placeholder = "Placeholder",
                        isError = false,
                        errorMessage = "Error text",
                    )
                }
            }
            onNodeWithText("Error text").assertDoesNotExist()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun textFieldCanBeDisabled() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaTextField(
                        value = TextFieldValue("test"),
                        onValueChange = {},
                        label = "Label",
                        placeholder = "Placeholder",
                        enabled = false,
                    )
                }
            }
            onNodeWithText("test").assertIsNotEnabled()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun textFieldIsEnabledByDefault() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaTextField(
                        value = TextFieldValue("test"),
                        onValueChange = {},
                        label = "Label",
                        placeholder = "Placeholder",
                    )
                }
            }
            onNodeWithText("test").assertIsEnabled()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun textFieldAcceptsInput() =
        runComposeUiTest {
            var currentValue by mutableStateOf(TextFieldValue(""))
            setContent {
                ConektaTheme {
                    ConektaTextField(
                        value = currentValue,
                        onValueChange = { currentValue = it },
                        label = "Label",
                        placeholder = "Placeholder",
                    )
                }
            }
            onNodeWithText("Placeholder").performTextInput("Hello")
            assert(currentValue.text == "Hello")
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun textFieldShowsErrorWithNullMessage() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaTextField(
                        value = TextFieldValue(""),
                        onValueChange = {},
                        label = "Label",
                        placeholder = "Placeholder",
                        isError = true,
                        errorMessage = null,
                    )
                }
            }
            // Should render without crash even with isError=true and null message
            onNodeWithText("Label").assertIsDisplayed()
        }
}
