package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.components.card.CardFieldsState
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_bank_transfer_reference_message
import io.conekta.compose.generated.resources.checkout_cash_points_message
import io.conekta.compose.generated.resources.checkout_loading
import io.conekta.compose.generated.resources.placeholder_cardholder_name_checkout
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.tokenizer.validators.ValidationMessages
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaymentMethodsSectionTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    private val validationMessages =
        ValidationMessages(
            required = "Required",
            cardMinLength = "Card too short",
            invalidCard = "Invalid card",
            expiryYearInvalid = "Invalid date",
            cvvMinLength = "Invalid CVV",
        )

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun paymentMethodsSectionShowsLoadingState() =
        runComposeUiTest {
            var loadingText = ""
            setContent {
                loadingText = stringResource(Res.string.checkout_loading)
                ConektaTheme {
                    PaymentMethodsSection(
                        methods = emptyList(),
                        allowedPaymentMethods = emptyList(),
                        selectedPaymentMethod = null,
                        onMethodSelected = {},
                        isLoading = true,
                        loadError = null,
                        cardFields = CardFieldsState(),
                        cashProviders = emptyList(),
                        cardValidationMessages = validationMessages,
                    )
                }
            }

            onNodeWithText(loadingText).assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun paymentMethodsSectionShowsProvidedErrorMessage() =
        runComposeUiTest {
            val errorMessage = "Custom load error"

            setContent {
                ConektaTheme {
                    PaymentMethodsSection(
                        methods = emptyList(),
                        allowedPaymentMethods = emptyList(),
                        selectedPaymentMethod = null,
                        onMethodSelected = {},
                        isLoading = false,
                        loadError = errorMessage,
                        cardFields = CardFieldsState(),
                        cashProviders = emptyList(),
                        cardValidationMessages = validationMessages,
                    )
                }
            }

            onNodeWithText(errorMessage).assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun paymentMethodsSectionCardInputValidatesAndClearsErrorsOnEmpty() =
        runComposeUiTest {
            val cardFields = CardFieldsState()
            setContent {
                ConektaTheme {
                    PaymentMethodsSection(
                        methods = listOf(CheckoutPaymentMethods.CARD),
                        allowedPaymentMethods = listOf(CheckoutPaymentMethods.CARD),
                        selectedPaymentMethod = CheckoutPaymentMethods.CARD,
                        onMethodSelected = {},
                        isLoading = false,
                        loadError = null,
                        cardFields = cardFields,
                        cashProviders = emptyList(),
                        cardValidationMessages = validationMessages,
                    )
                }
            }

            val inputs = onAllNodes(hasSetTextAction())
            inputs[0].performTextInput("1234")
            inputs[1].performTextInput("12")
            inputs[2].performTextInput("1")
            waitForIdle()

            onNodeWithText(validationMessages.cardMinLength).assertIsDisplayed()
            onNodeWithText(validationMessages.expiryYearInvalid).assertIsDisplayed()
            onNodeWithText(validationMessages.cvvMinLength).assertIsDisplayed()

            inputs[0].performTextClearance()
            inputs[1].performTextClearance()
            inputs[2].performTextClearance()
            waitForIdle()

            onAllNodesWithText(validationMessages.cardMinLength).assertCountEquals(0)
            onAllNodesWithText(validationMessages.expiryYearInvalid).assertCountEquals(0)
            onAllNodesWithText(validationMessages.cvvMinLength).assertCountEquals(0)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun paymentMethodsSectionShowsCardItemWhenNotSelected() =
        runComposeUiTest {
            var cardholderPlaceholder = ""
            setContent {
                cardholderPlaceholder = stringResource(Res.string.placeholder_cardholder_name_checkout)
                ConektaTheme {
                    PaymentMethodsSection(
                        methods = listOf(CheckoutPaymentMethods.CARD),
                        allowedPaymentMethods = listOf(CheckoutPaymentMethods.CARD),
                        selectedPaymentMethod = null,
                        onMethodSelected = {},
                        isLoading = false,
                        loadError = null,
                        cardFields = CardFieldsState(),
                        cashProviders = emptyList(),
                        cardValidationMessages = validationMessages,
                    )
                }
            }

            onAllNodesWithText(cardholderPlaceholder, substring = true).assertCountEquals(0)
            onAllNodesWithText("MM/YY", substring = true).assertCountEquals(0)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun paymentMethodsSectionShowsCashAndBankReferenceWhenSelected() =
        runComposeUiTest {
            var cashPointsText = ""
            setContent {
                cashPointsText = stringResource(Res.string.checkout_cash_points_message)
                ConektaTheme {
                    PaymentMethodsSection(
                        methods = listOf(CheckoutPaymentMethods.CASH, CheckoutPaymentMethods.BANK_TRANSFER),
                        allowedPaymentMethods =
                            listOf(
                                CheckoutPaymentMethods.CASH,
                                CheckoutPaymentMethods.BANK_TRANSFER,
                            ),
                        selectedPaymentMethod = CheckoutPaymentMethods.CASH,
                        onMethodSelected = {},
                        isLoading = false,
                        loadError = null,
                        cardFields = CardFieldsState(),
                        cashProviders = emptyList(),
                        cardValidationMessages = validationMessages,
                    )
                }
            }

            onNodeWithText(cashPointsText, substring = true).assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun paymentMethodsSectionShowsBankReferenceWhenBankTransferSelected() =
        runComposeUiTest {
            var cashPointsText = ""
            var bankTransferReferenceText = ""
            setContent {
                cashPointsText = stringResource(Res.string.checkout_cash_points_message)
                bankTransferReferenceText = stringResource(Res.string.checkout_bank_transfer_reference_message)
                ConektaTheme {
                    PaymentMethodsSection(
                        methods = listOf(CheckoutPaymentMethods.CASH, CheckoutPaymentMethods.BANK_TRANSFER),
                        allowedPaymentMethods =
                            listOf(
                                CheckoutPaymentMethods.CASH,
                                CheckoutPaymentMethods.BANK_TRANSFER,
                            ),
                        selectedPaymentMethod = CheckoutPaymentMethods.BANK_TRANSFER,
                        onMethodSelected = {},
                        isLoading = false,
                        loadError = null,
                        cardFields = CardFieldsState(),
                        cashProviders = emptyList(),
                        cardValidationMessages = validationMessages,
                    )
                }
            }

            onNodeWithText(bankTransferReferenceText).assertIsDisplayed()
            onAllNodesWithText(cashPointsText, substring = true).assertCountEquals(0)
        }
}
