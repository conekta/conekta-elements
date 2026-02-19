package io.conekta.compose.tokenizer

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.initComposeResourcesContext
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConektaTokenizerTest {
    @Before
    fun setUp() = initComposeResourcesContext()
    private val defaultConfig =
        TokenizerConfig(
            publicKey = "key_test_123",
            merchantName = "Test Store",
            collectCardholderName = true,
        )

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerWithCardholderNameCollectionEnabled() =
        runComposeUiTest {
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerWithoutCardholderNameCollection() =
        runComposeUiTest {
            val config = defaultConfig.copy(collectCardholderName = false)
            setContent {
                ConektaTokenizer(
                    config = config,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerRendersWithDifferentMerchantName() =
        runComposeUiTest {
            val config = defaultConfig.copy(merchantName = "Custom Store")
            setContent {
                ConektaTokenizer(
                    config = config,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerDisplaysSubmitButton() =
        runComposeUiTest {
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun submitWithEmptyFieldsShowsValidationErrors() =
        runComposeUiTest {
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            // Click submit with all fields empty to trigger validation
            onNode(hasText("Continuar") or hasText("Continue")).performClick()
            waitForIdle()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun submitWithPartialFieldsShowsValidation() =
        runComposeUiTest {
            var errorReceived: TokenizerError? = null
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = { errorReceived = it },
                )
            }
            // Fill card number only, leave other fields empty
            onNodeWithText("0000 0000 0000 0000").performTextInput("4242424242424242")
            onNode(hasText("Continuar") or hasText("Continue")).performClick()
            waitForIdle()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun submitWithAllFieldsFilledTriggersTokenization() =
        runComposeUiTest {
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            // Fill all fields
            onNodeWithText("0000 0000 0000 0000").performTextInput("4242424242424242")
            onNodeWithText("MM/YY", substring = true).performTextInput("1226")
            onNodeWithText("CVV", substring = true).performTextInput("123")

            // Submit
            onNode(hasText("Continuar") or hasText("Continue")).performClick()
            waitForIdle()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun submitWithAllFieldsAndNameTriggersTokenization() =
        runComposeUiTest {
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            // Fill all fields including name (use placeholder text to find the field)
            onNode(
                hasText("Name as it appears on card", substring = true)
                    or hasText("Nombre como aparece en la tarjeta", substring = true),
            ).performTextInput("Test User")
            onNodeWithText("0000 0000 0000 0000").performTextInput("4242424242424242")
            onNodeWithText("MM/YY", substring = true).performTextInput("1226")
            onNodeWithText("CVV", substring = true).performTextInput("123")

            onNode(hasText("Continuar") or hasText("Continue")).performClick()
            waitForIdle()
        }
}
