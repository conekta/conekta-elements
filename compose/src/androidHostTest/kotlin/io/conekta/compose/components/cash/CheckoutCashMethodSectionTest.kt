package io.conekta.compose.components.cash

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_cash_provider_more_link
import io.conekta.compose.generated.resources.checkout_method_cash
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import io.conekta.elements.checkout.models.CheckoutProvider
import io.conekta.elements.checkout.models.ProductTypes
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CheckoutCashMethodSectionTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutCashMethodItemCallsOnClick() =
        runComposeUiTest {
            var clicked = false
            var cashMethodLabel = ""
            setContent {
                cashMethodLabel = stringResource(Res.string.checkout_method_cash)
                ConektaTheme {
                    CheckoutCashMethodItem(
                        selected = false,
                        onClick = { clicked = true },
                    )
                }
            }

            onNodeWithText(cashMethodLabel).performClick()

            assertTrue(clicked)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutCashMethodSectionExpandsMoreProvidersList() =
        runComposeUiTest {
            var moreProvidersLinkText = ""
            setContent {
                moreProvidersLinkText = stringResource(Res.string.checkout_cash_provider_more_link)
                ConektaTheme {
                    CheckoutCashMethodSection(
                        onSelect = {},
                        providers =
                            listOf(
                                CheckoutProvider(
                                    id = "provider_1",
                                    name = "Cash In",
                                    paymentMethod = "cash",
                                    productType = ProductTypes.CASH_IN,
                                ),
                            ),
                    )
                }
            }

            onNodeWithText(moreProvidersLinkText).assertIsDisplayed().performClick()

            onNodeWithText("Waldos", substring = true).assertIsDisplayed()
        }
}
