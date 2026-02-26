package io.conekta.compose.components.cash

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_cash_provider_more_link
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.testfixtures.CheckoutTestFixtures
import io.conekta.compose.testfixtures.checkoutResultFixture
import io.conekta.compose.testfixtures.orderResultFixture
import io.conekta.compose.testfixtures.paymentMethodFixture
import io.conekta.compose.theme.ConektaTheme
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.ProductTypes
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CashSuccessComponentsTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cashSuccessPaySummaryCardDisplaysFormattedAmount() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CashSuccessPaySummaryCard(
                        orderResult = orderResultFixture(),
                        checkoutResult =
                            checkoutResultFixture(
                                amount = 50000,
                                allowedPaymentMethods = listOf(CheckoutPaymentMethods.CASH),
                            ),
                    )
                }
            }

            onNodeWithText("\$500.00 MXN").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cashInSuccessCardDisplaysBarcodeReferenceAndExpandedProviders() =
        runComposeUiTest {
            var moreProvidersLinkText = ""
            setContent {
                moreProvidersLinkText = stringResource(Res.string.checkout_cash_provider_more_link)
                ConektaTheme {
                    CashInSuccessCard(
                        paymentMethod =
                            paymentMethodFixture(
                                reference = CheckoutTestFixtures.REFERENCE_20_A,
                                barcodeUrl = "https://example.com/barcode.png",
                                productType = ProductTypes.CASH_IN,
                            ),
                    )
                }
            }

            onNodeWithText(CheckoutTestFixtures.REFERENCE_20_A).assertIsDisplayed()
            onNodeWithContentDescription("Barcode for payment reference").assertIsDisplayed()

            onNodeWithText(moreProvidersLinkText).performClick()
            onNodeWithText("Waldos", substring = true).assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun bbvaCashInSuccessCardDisplaysAgreementAndReference() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    BbvaCashInSuccessCard(
                        paymentMethod =
                            paymentMethodFixture(
                                agreement = "1234567",
                                reference = CheckoutTestFixtures.REFERENCE_20_B,
                            ),
                    )
                }
            }

            onNodeWithText("1234567", substring = true).assertIsDisplayed()
            onNodeWithText(CheckoutTestFixtures.REFERENCE_20_B, substring = true).assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cashSuccessContentShowsCashInCardAndEmailToast() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CashSuccessContent(
                        orderResult =
                            orderResultFixture(
                                paymentMethod =
                                    paymentMethodFixture(
                                        productType = ProductTypes.CASH_IN,
                                        reference = CheckoutTestFixtures.REFERENCE_20_C,
                                        barcodeUrl = "https://example.com/barcode.png",
                                    ),
                            ),
                        checkoutResult =
                            checkoutResultFixture(
                                allowedPaymentMethods = listOf(CheckoutPaymentMethods.CASH),
                                email = "cash@conekta.com",
                            ),
                    )
                }
            }

            onNodeWithText(CheckoutTestFixtures.REFERENCE_20_C).assertExists()
            onNodeWithText("cash@conekta.com", substring = true).assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cashSuccessContentShowsBbvaCardWhenProductTypeMatches() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CashSuccessContent(
                        orderResult =
                            orderResultFixture(
                                paymentMethod =
                                    paymentMethodFixture(
                                        productType = ProductTypes.BBVA_CASH_IN,
                                        agreement = "999",
                                        reference = CheckoutTestFixtures.REFERENCE_20_D,
                                    ),
                            ),
                        checkoutResult =
                            checkoutResultFixture(
                                allowedPaymentMethods = listOf(CheckoutPaymentMethods.CASH),
                            ),
                    )
                }
            }

            onNodeWithText("999", substring = true).assertExists()
            onNodeWithText(CheckoutTestFixtures.REFERENCE_20_D, substring = true).assertExists()
        }
}
