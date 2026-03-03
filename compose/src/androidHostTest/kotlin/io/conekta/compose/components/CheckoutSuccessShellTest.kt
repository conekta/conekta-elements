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
            onNodeWithText("Success content body").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutSuccessShellShowsProtectionSheetWhenInitiallyEnabled() =
        runComposeUiTest {
            val merchantName = "Merchant Coverage Test"
            val checkoutResult =
                checkoutResultFixture(
                    orderId = "ord_test_2",
                    checkoutId = "chk_test_2",
                    name = merchantName,
                    amount = 1099,
                    allowedPaymentMethods = emptyList(),
                )

            setContent {
                ConektaTheme {
                    CheckoutSuccessShell(
                        checkoutResult = checkoutResult,
                        merchantName = merchantName,
                        currentLanguageTag = "es",
                        onLanguageSelected = {},
                        initialShowProtectionSheet = true,
                        protectionSheetRenderer = { merchant, _ ->
                            Text("Sheet renderer for $merchant")
                        },
                    ) {
                        Text("Sheet coverage content")
                    }
                }
            }
            onNodeWithText("Sheet renderer for $merchantName").assertIsDisplayed()
        }
}
