package io.conekta.compose.checkout

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_method_card
import io.conekta.compose.generated.resources.placeholder_cardholder_name_checkout
import io.conekta.compose.initComposeResourcesContext
import io.conekta.elements.checkout.api.CheckoutApiException
import io.conekta.elements.checkout.api.CheckoutApiService
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.checkout.models.CurrencyCodes
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ConektaCheckoutTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutRendersSupportedMethodsAndCardSection() =
        runComposeUiTest {
            var selectedMethod = ""
            var capturedError: CheckoutError? = null
            var cardMethodLabel = ""
            var cardholderPlaceholder = ""

            val config =
                CheckoutConfig(
                    checkoutRequestId = "dc5baf10-0f2b-4378-9f74-afa6bb418198",
                    publicKey = "key_test_123",
                    jwtToken = "jwt_test",
                    baseUrl = "https://test.conekta.com/",
                    languageTag = "en",
                )

            setContent {
                cardMethodLabel = stringResource(Res.string.checkout_method_card)
                cardholderPlaceholder = stringResource(Res.string.placeholder_cardholder_name_checkout)
                ConektaCheckout(
                    config = config,
                    onPaymentMethodSelected = { selectedMethod = it },
                    onError = { capturedError = it },
                    checkoutApiServiceFactory = {
                        object : CheckoutApiService(it) {
                            override suspend fun fetchCheckout(): Result<CheckoutResult> =
                                Result.success(
                                    CheckoutResult(
                                        orderId = "ord_2zb4KeLHjraBbRJgs",
                                        checkoutId = "dc5baf10-0f2b-4378-9f74-afa6bb418198",
                                        amount = 12000,
                                        currency = CurrencyCodes.MXN,
                                        allowedPaymentMethods =
                                            listOf(
                                                CheckoutPaymentMethods.CARD,
                                                "bnpl",
                                                CheckoutPaymentMethods.CASH,
                                                "pay_by_bank",
                                                CheckoutPaymentMethods.BANK_TRANSFER,
                                                "apple",
                                            ),
                                    ),
                                )
                        }
                    },
                )
            }

            waitForIdle()

            onNodeWithText(cardMethodLabel).assertIsDisplayed()
            onNodeWithText("pay_by_bank").assertDoesNotExist()
            onNodeWithText("bnpl").assertDoesNotExist()
            onNodeWithText("apple").assertDoesNotExist()

            onNodeWithText(cardholderPlaceholder).assertExists()
            onNodeWithText("0000 0000 0000 0000").assertExists()
            assertEquals(CheckoutPaymentMethods.CARD, selectedMethod)
            assertEquals(null, capturedError)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutOnPayClickCreatesOrderForCashMethod() =
        runComposeUiTest {
            var selectedMethod = ""
            var createdOrder: CheckoutOrderResult? = null
            var createOrderCalls = 0

            val config =
                CheckoutConfig(
                    checkoutRequestId = "a1160312-d24d-40dd-b08b-193e4f4fddde",
                    publicKey = "key_test_123",
                    jwtToken = "jwt_test",
                    baseUrl = "https://test.conekta.com/",
                )

            setContent {
                ConektaCheckout(
                    config = config,
                    onPaymentMethodSelected = { selectedMethod = it },
                    onError = {},
                    onOrderCreated = { createdOrder = it },
                    checkoutApiServiceFactory = {
                        object : CheckoutApiService(it) {
                            override suspend fun fetchCheckout(): Result<CheckoutResult> =
                                Result.success(
                                    CheckoutResult(
                                        orderId = "ord_cash",
                                        checkoutId = "chk_cash",
                                        amount = 12000,
                                        currency = CurrencyCodes.MXN,
                                        allowedPaymentMethods = listOf(CheckoutPaymentMethods.CASH),
                                    ),
                                )

                            override suspend fun createOrder(
                                paymentMethod: String,
                                tokenId: String?,
                            ): Result<CheckoutOrderResult> {
                                createOrderCalls += 1
                                return Result.success(CheckoutOrderResult(orderId = "ord_created_cash"))
                            }
                        }
                    },
                )
            }

            repeat(5) {
                waitForIdle()
                Thread.sleep(50)
            }
            onNodeWithTag("checkout_pay_button").assertIsEnabled()
            onNodeWithTag("checkout_pay_button").performSemanticsAction(SemanticsActions.OnClick)
            mainClock.advanceTimeBy(1_000)
            waitForIdle()

            assertEquals(CheckoutPaymentMethods.CASH, selectedMethod)
            assertEquals(1, createOrderCalls)
            assertEquals("ord_created_cash", createdOrder?.orderId)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutOnPayClickShowsApiErrorToastWhenCreateOrderFails() =
        runComposeUiTest {
            var capturedError: CheckoutError? = null
            val apiMessage = "No fue posible crear la orden"
            val config =
                CheckoutConfig(
                    checkoutRequestId = "58fd8107-8b9c-4359-9ba7-a025e103ef93",
                    publicKey = "key_test_123",
                    jwtToken = "jwt_test",
                    baseUrl = "https://test.conekta.com/",
                )

            setContent {
                ConektaCheckout(
                    config = config,
                    onPaymentMethodSelected = {},
                    onError = { capturedError = it },
                    onOrderCreated = {},
                    checkoutApiServiceFactory = {
                        object : CheckoutApiService(it) {
                            override suspend fun fetchCheckout(): Result<CheckoutResult> =
                                Result.success(
                                    CheckoutResult(
                                        orderId = "ord_cash",
                                        checkoutId = "chk_cash",
                                        amount = 12000,
                                        currency = CurrencyCodes.MXN,
                                        allowedPaymentMethods = listOf(CheckoutPaymentMethods.CASH),
                                    ),
                                )

                            override suspend fun createOrder(
                                paymentMethod: String,
                                tokenId: String?,
                            ): Result<CheckoutOrderResult> =
                                Result.failure(
                                    CheckoutApiException(
                                        CheckoutError.ApiError(
                                            code = "api_error",
                                            message = apiMessage,
                                        ),
                                    ),
                                )
                        }
                    },
                )
            }

            waitForIdle()
            onNodeWithTag("checkout_pay_button").performSemanticsAction(SemanticsActions.OnClick)
            mainClock.advanceTimeBy(1_000)
            waitForIdle()

            onNodeWithText(apiMessage).assertIsDisplayed()
            val error = assertIs<CheckoutError.ApiError>(capturedError)
            assertEquals("api_error", error.code)
            assertEquals(apiMessage, error.message)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutOnPayClickShowsNetworkErrorToastWhenCreateOrderThrowsException() =
        runComposeUiTest {
            var capturedError: CheckoutError? = null
            val thrownMessage = "create order exploded"
            val config =
                CheckoutConfig(
                    checkoutRequestId = "6b0d1f81-8e8d-4908-8ae2-6f1778ce1e16",
                    publicKey = "key_test_123",
                    jwtToken = "jwt_test",
                    baseUrl = "https://test.conekta.com/",
                )

            setContent {
                ConektaCheckout(
                    config = config,
                    onPaymentMethodSelected = {},
                    onError = { capturedError = it },
                    onOrderCreated = {},
                    checkoutApiServiceFactory = {
                        object : CheckoutApiService(it) {
                            override suspend fun fetchCheckout(): Result<CheckoutResult> =
                                Result.success(
                                    CheckoutResult(
                                        orderId = "ord_cash",
                                        checkoutId = "chk_cash",
                                        amount = 12000,
                                        currency = CurrencyCodes.MXN,
                                        allowedPaymentMethods = listOf(CheckoutPaymentMethods.CASH),
                                    ),
                                )

                            override suspend fun createOrder(
                                paymentMethod: String,
                                tokenId: String?,
                            ): Result<CheckoutOrderResult> = throw IllegalStateException(thrownMessage)
                        }
                    },
                )
            }

            waitForIdle()
            onNodeWithTag("checkout_pay_button").performSemanticsAction(SemanticsActions.OnClick)
            mainClock.advanceTimeBy(1_000)
            waitForIdle()

            onNodeWithText(thrownMessage).assertIsDisplayed()
            val error = assertIs<CheckoutError.NetworkError>(capturedError)
            assertEquals(thrownMessage, error.message)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutUsesConfigLanguageTagWhenProvided() =
        runComposeUiTest {
            var serviceConfigLanguageTag: String? = null
            val config =
                CheckoutConfig(
                    checkoutRequestId = "lang-en-checkout",
                    publicKey = "key_test_123",
                    jwtToken = "jwt_test",
                    baseUrl = "https://test.conekta.com/",
                    languageTag = "en-US",
                )

            setContent {
                ConektaCheckout(
                    config = config,
                    onPaymentMethodSelected = {},
                    onError = {},
                    checkoutApiServiceFactory = { checkoutConfig ->
                        serviceConfigLanguageTag = checkoutConfig.languageTag
                        object : CheckoutApiService(checkoutConfig) {
                            override suspend fun fetchCheckout(): Result<CheckoutResult> =
                                Result.success(
                                    CheckoutResult(
                                        orderId = "ord_lang_en",
                                        checkoutId = "chk_lang_en",
                                        amount = 12000,
                                        currency = CurrencyCodes.MXN,
                                        allowedPaymentMethods = listOf(CheckoutPaymentMethods.CARD),
                                    ),
                                )
                        }
                    },
                )
            }

            waitForIdle()
            assertEquals("en", serviceConfigLanguageTag)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutResolvesAutoLanguageTagUsingDeviceLanguage() =
        runComposeUiTest {
            var serviceConfigLanguageTag: String? = null
            val config =
                CheckoutConfig(
                    checkoutRequestId = "lang-auto-checkout",
                    publicKey = "key_test_123",
                    jwtToken = "jwt_test",
                    baseUrl = "https://test.conekta.com/",
                    languageTag = "auto",
                )

            setContent {
                ConektaCheckout(
                    config = config,
                    onPaymentMethodSelected = {},
                    onError = {},
                    checkoutApiServiceFactory = { checkoutConfig ->
                        serviceConfigLanguageTag = checkoutConfig.languageTag
                        object : CheckoutApiService(checkoutConfig) {
                            override suspend fun fetchCheckout(): Result<CheckoutResult> =
                                Result.success(
                                    CheckoutResult(
                                        orderId = "ord_lang_auto",
                                        checkoutId = "chk_lang_auto",
                                        amount = 12000,
                                        currency = CurrencyCodes.MXN,
                                        allowedPaymentMethods = listOf(CheckoutPaymentMethods.CARD),
                                    ),
                                )
                        }
                    },
                )
            }

            waitForIdle()
            assertNotEquals("auto", serviceConfigLanguageTag)
            assertTrue(serviceConfigLanguageTag == "es" || serviceConfigLanguageTag == "en")
        }
}
