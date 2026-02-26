package io.conekta.compose.components.card

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_card_purchase_completed
import io.conekta.compose.generated.resources.success_card_title
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.testfixtures.CheckoutTestFixtures
import io.conekta.compose.testfixtures.checkoutResultFixture
import io.conekta.compose.theme.ConektaTheme
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CardSuccessContentTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardSuccessContentDisplaysTitleAndPurchaseMessage() =
        runComposeUiTest {
            val merchantName = CheckoutTestFixtures.MERCHANT_NAME
            var successTitle = ""
            var purchaseMessage = ""

            setContent {
                successTitle = stringResource(Res.string.success_card_title)
                purchaseMessage = stringResource(Res.string.success_card_purchase_completed, merchantName, "$500.00")
                ConektaTheme {
                    CardSuccessContent(
                        checkoutResult =
                            checkoutResultFixture(
                                orderId = "ord_card_1",
                                checkoutId = "chk_card_1",
                                name = merchantName,
                                amount = 50000,
                                allowedPaymentMethods = listOf(CheckoutPaymentMethods.CARD),
                            ),
                        merchantName = merchantName,
                    )
                }
            }

            onNodeWithText(successTitle).assertIsDisplayed()
            onNodeWithText(purchaseMessage).assertIsDisplayed()
        }
}
