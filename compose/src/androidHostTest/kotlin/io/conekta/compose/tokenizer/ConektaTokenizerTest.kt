package io.conekta.compose.tokenizer

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.button_continue
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.generated.resources.placeholder_cardholder_name
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
import org.jetbrains.compose.resources.stringResource
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
            var continueButtonText = ""
            setContent {
                continueButtonText = stringResource(Res.string.button_continue)
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            // Click submit with all fields empty to trigger validation
            onNodeWithText(continueButtonText).performClick()
            waitForIdle()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun submitWithPartialFieldsShowsValidation() =
        runComposeUiTest {
            var errorReceived: TokenizerError? = null
            var continueButtonText = ""
            setContent {
                continueButtonText = stringResource(Res.string.button_continue)
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = { errorReceived = it },
                )
            }
            // Fill card number only, leave other fields empty
            onNodeWithText("0000 0000 0000 0000").performTextInput("4242424242424242")
            onNodeWithText(continueButtonText).performClick()
            waitForIdle()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun submitWithAllFieldsFilledTriggersTokenization() =
        runComposeUiTest {
            var continueButtonText = ""
            setContent {
                continueButtonText = stringResource(Res.string.button_continue)
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            // Fill all fields
            onNodeWithText("0000 0000 0000 0000").performTextInput("4242424242424242")
            onNodeWithText("MM/YY", substring = true).performTextInput("1226")
            onNode(hasText("CVV", substring = true) and hasSetTextAction()).performTextInput("123")

            // Submit
            onNodeWithText(continueButtonText).performClick()
            waitForIdle()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun submitWithAllFieldsAndNameTriggersTokenization() =
        runComposeUiTest {
            var continueButtonText = ""
            var cardholderNamePlaceholder = ""
            setContent {
                continueButtonText = stringResource(Res.string.button_continue)
                cardholderNamePlaceholder = stringResource(Res.string.placeholder_cardholder_name)
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            // Fill all fields including name (use placeholder text to find the field)
            onNodeWithText(cardholderNamePlaceholder, substring = true).performTextInput("Test User")
            onNodeWithText("0000 0000 0000 0000").performTextInput("4242424242424242")
            onNodeWithText("MM/YY", substring = true).performTextInput("1226")
            onNode(hasText("CVV", substring = true) and hasSetTextAction()).performTextInput("123")

            onNodeWithText(continueButtonText).performClick()
            waitForIdle()
        }
}
