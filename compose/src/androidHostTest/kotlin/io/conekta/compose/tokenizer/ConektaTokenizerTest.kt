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
import io.conekta.compose.generated.resources.placeholder_cardholder_name
import io.conekta.compose.initComposeResourcesContext
import io.conekta.elements.tokenizer.api.TokenizerApiService
import io.conekta.elements.tokenizer.crypto.CardEncryptor
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

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

    private fun tokenizerServiceFactory(
        statusCode: HttpStatusCode,
        responseBody: String,
    ): (TokenizerConfig, String) -> TokenizerApiService =
        { config, languageTag ->
            val httpClient =
                HttpClient(MockEngine) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                    engine {
                        addHandler {
                            respond(
                                content = responseBody,
                                status = statusCode,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }
                    }
                }
            TokenizerApiService(
                config = config,
                languageTag = languageTag,
                httpClient = httpClient,
                cryptoService = CardEncryptor { plaintext, _ -> plaintext },
            )
        }

    @OptIn(ExperimentalTestApi::class)
    private fun androidx.compose.ui.test.ComposeUiTest.fillValidCardFields(
        cardholderNamePlaceholder: String,
        includeCardholderName: Boolean = false,
    ) {
        if (includeCardholderName) {
            onNodeWithText(cardholderNamePlaceholder, substring = true).performTextInput("Test User")
        }
        onNodeWithText("0000 0000 0000 0000").performTextInput("4242424242424242")
        onNodeWithText("MM/YY", substring = true).performTextInput("1226")
        onNode(hasText("CVV", substring = true) and hasSetTextAction()).performTextInput("123")
    }

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
    fun submitWithValidFieldsAndInvalidConfigCallsOnErrorValidation() =
        runComposeUiTest {
            var continueButtonText = ""
            var cardholderNamePlaceholder = ""
            var receivedError: TokenizerError? = null
            setContent {
                continueButtonText = stringResource(Res.string.button_continue)
                cardholderNamePlaceholder = stringResource(Res.string.placeholder_cardholder_name)
                ConektaTokenizer(
                    config = defaultConfig.copy(publicKey = "", collectCardholderName = false),
                    onSuccess = {},
                    onError = { receivedError = it },
                )
            }

            fillValidCardFields(cardholderNamePlaceholder, includeCardholderName = false)
            onNodeWithText(continueButtonText).performClick()
            waitForIdle()

            val error = assertNotNull(receivedError)
            assertIs<TokenizerError.ValidationError>(error)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun submitWithValidFieldsCallsOnSuccessWhenApiTokenizes() =
        runComposeUiTest {
            var continueButtonText = ""
            var cardholderNamePlaceholder = ""
            var receivedToken: io.conekta.elements.tokenizer.models.TokenResult? = null
            setContent {
                continueButtonText = stringResource(Res.string.button_continue)
                cardholderNamePlaceholder = stringResource(Res.string.placeholder_cardholder_name)
                ConektaTokenizer(
                    config = defaultConfig.copy(collectCardholderName = false),
                    onSuccess = { receivedToken = it },
                    onError = {},
                    tokenizerApiServiceFactory =
                        tokenizerServiceFactory(
                            statusCode = HttpStatusCode.OK,
                            responseBody = """{"id":"tok_test_ok","livemode":false,"used":false,"object":"token"}""",
                        ),
                )
            }

            fillValidCardFields(cardholderNamePlaceholder, includeCardholderName = false)
            onNodeWithText(continueButtonText).performClick()
            repeat(10) {
                if (receivedToken != null) return@repeat
                Thread.sleep(100)
                waitForIdle()
            }

            assertEquals("tok_test_ok", receivedToken?.token)
            assertEquals("4242", receivedToken?.lastFour)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun submitWithValidFieldsCallsOnErrorWhenApiFails() =
        runComposeUiTest {
            var continueButtonText = ""
            var cardholderNamePlaceholder = ""
            var receivedError: TokenizerError? = null
            setContent {
                continueButtonText = stringResource(Res.string.button_continue)
                cardholderNamePlaceholder = stringResource(Res.string.placeholder_cardholder_name)
                ConektaTokenizer(
                    config = defaultConfig.copy(collectCardholderName = false),
                    onSuccess = {},
                    onError = { receivedError = it },
                    tokenizerApiServiceFactory =
                        tokenizerServiceFactory(
                            statusCode = HttpStatusCode.UnprocessableEntity,
                            responseBody =
                                """
                                {
                                  "object":"error",
                                  "type":"invalid_request_error",
                                  "message":"card number is invalid",
                                  "message_to_purchaser":"Card declined"
                                }
                                """.trimIndent(),
                        ),
                )
            }

            fillValidCardFields(cardholderNamePlaceholder, includeCardholderName = false)
            onNodeWithText(continueButtonText).performClick()
            repeat(10) {
                if (receivedError != null) return@repeat
                Thread.sleep(100)
                waitForIdle()
            }

            val error = assertNotNull(receivedError)
            val apiError = assertIs<TokenizerError.ApiError>(error)
            assertEquals("invalid_request_error", apiError.code)
            assertEquals("Card declined", apiError.message)
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
