package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.components.card.CardFieldsState
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_loading
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
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
}
