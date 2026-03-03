package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_discount_label
import io.conekta.compose.generated.resources.checkout_quantity_label
import io.conekta.compose.generated.resources.checkout_shipping_label
import io.conekta.compose.generated.resources.checkout_tax_label
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import io.conekta.elements.checkout.models.CheckoutAmountLine
import io.conekta.elements.checkout.models.CheckoutLineItem
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CheckoutTotalRowTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutTotalRowDisplaysAmountText() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CheckoutTotalRow(amountText = "\$120.00")
                }
            }

            onNodeWithText("\$120.00").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutTotalRowExpandsBreakdownWhenClicked() =
        runComposeUiTest {
            var quantityLabel = ""
            setContent {
                quantityLabel = stringResource(Res.string.checkout_quantity_label)
                ConektaTheme {
                    CheckoutTotalRow(
                        amountText = "\$120.00",
                        lineItems =
                            listOf(
                                CheckoutLineItem(name = "Producto de prueba", quantity = 1, unitPrice = 12000),
                            ),
                    )
                }
            }

            onNode(hasText("Total") and hasClickAction()).performClick()

            onNodeWithText(quantityLabel).assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutTotalRowShowsTaxShippingAndDiscountLinesWithFallbackLabels() =
        runComposeUiTest {
            var taxLabel = ""
            var shippingLabel = ""
            var discountLabel = ""

            setContent {
                taxLabel = stringResource(Res.string.checkout_tax_label)
                shippingLabel = stringResource(Res.string.checkout_shipping_label)
                discountLabel = stringResource(Res.string.checkout_discount_label)
                ConektaTheme {
                    CheckoutTotalRow(
                        amountText = "\$100.00",
                        lineItems = listOf(CheckoutLineItem(name = "Producto", quantity = 1, unitPrice = 10000)),
                        taxLines = listOf(CheckoutAmountLine(description = "", amount = 111)),
                        shippingLines = listOf(CheckoutAmountLine(description = "", amount = 222)),
                        discountLines = listOf(CheckoutAmountLine(description = "", amount = 333)),
                    )
                }
            }

            onNode(hasText("Total") and hasClickAction()).performClick()

            onNodeWithText(taxLabel).assertIsDisplayed()
            onNodeWithText("\$1.11").assertIsDisplayed()
            onNodeWithText(shippingLabel).assertIsDisplayed()
            onNodeWithText("\$2.22").assertIsDisplayed()
            onNodeWithText(discountLabel).assertIsDisplayed()
            onNodeWithText("-\$3.33").assertIsDisplayed()
        }
}
