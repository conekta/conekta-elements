package io.conekta.compose.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.testfixtures.CheckoutTestFixtures
import io.conekta.compose.testfixtures.checkoutResultFixture
import io.conekta.compose.theme.ConektaTheme
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CheckoutSuccessShellTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutSuccessShellDisplaysAmountDetailAndContent() =
        runComposeUiTest {
            val checkoutResult =
                checkoutResultFixture(
                    orderId = "ord_test_1",
                    checkoutId = "chk_test_1",
                    name = CheckoutTestFixtures.MERCHANT_NAME,
                    amount = 12345,
                    allowedPaymentMethods = emptyList(),
                )

            setContent {
                ConektaTheme {
                    CheckoutSuccessShell(
                        checkoutResult = checkoutResult,
                        merchantName = CheckoutTestFixtures.MERCHANT_NAME,
                        currentLanguageTag = "es",
                        onLanguageSelected = {},
                    ) {
                        Text("Success content body")
                    }
                }
            }

            onNodeWithText("\$123.45").assertIsDisplayed()
            onNodeWithText(CheckoutTestFixtures.MERCHANT_NAME).assertIsDisplayed()
            onNodeWithText("Success content body").assertIsDisplayed()
        }
}
