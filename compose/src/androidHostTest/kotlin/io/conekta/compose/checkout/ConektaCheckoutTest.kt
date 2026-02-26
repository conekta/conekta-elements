package io.conekta.compose.checkout

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_method_card
import io.conekta.compose.generated.resources.placeholder_cardholder_name_checkout
import io.conekta.elements.checkout.api.CheckoutApiService
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.checkout.models.CurrencyCodes
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

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
}
