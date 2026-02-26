package io.conekta.compose.components.banktransfer

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_bank_transfer_clabe_copied
import io.conekta.compose.generated.resources.success_bank_transfer_copy_number
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.testfixtures.CheckoutTestFixtures
import io.conekta.compose.testfixtures.checkoutResultFixture
import io.conekta.compose.testfixtures.orderResultFixture
import io.conekta.compose.testfixtures.paymentMethodFixture
import io.conekta.compose.theme.ConektaTheme
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BankTransferSuccessContentTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun bankTransferSuccessContentDisplaysEmailAndClabe() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    BankTransferSuccessContent(
                        orderResult =
                            orderResultFixture(
                                orderId = "ord_bt_1",
                                paymentMethod =
                                    paymentMethodFixture(
                                        clabe = CheckoutTestFixtures.REFERENCE_20_A,
                                        reference = CheckoutTestFixtures.REFERENCE_20_A,
                                        expiresAt = 1735689540L,
                                    ),
                            ),
                        checkoutResult =
                            checkoutResultFixture(
                                orderId = "ord_bt_1",
                                checkoutId = "chk_bt_1",
                                name = CheckoutTestFixtures.MERCHANT_NAME,
                                amount = 50000,
                                allowedPaymentMethods = listOf(CheckoutPaymentMethods.BANK_TRANSFER),
                                email = CheckoutTestFixtures.DEFAULT_CHECKOUT_EMAIL,
                            ),
                    )
                }
            }

            onNodeWithText(CheckoutTestFixtures.DEFAULT_CHECKOUT_EMAIL, substring = true).assertIsDisplayed()
            onNodeWithText(CheckoutTestFixtures.REFERENCE_20_A, substring = true).assertIsDisplayed()
            onNodeWithText("\$500.00").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun bankTransferSuccessContentShowsCopyToastWhenCopyIsTapped() =
        runComposeUiTest {
            var copyLabel = ""
            var copiedToastText = ""
            setContent {
                copyLabel = stringResource(Res.string.success_bank_transfer_copy_number)
                copiedToastText = stringResource(Res.string.success_bank_transfer_clabe_copied)
                ConektaTheme {
                    BankTransferSuccessContent(
                        orderResult =
                            orderResultFixture(
                                orderId = "ord_bt_1",
                                paymentMethod =
                                    paymentMethodFixture(
                                        clabe = CheckoutTestFixtures.REFERENCE_20_A,
                                        reference = CheckoutTestFixtures.REFERENCE_20_A,
                                        expiresAt = 1735689540L,
                                    ),
                            ),
                        checkoutResult =
                            checkoutResultFixture(
                                orderId = "ord_bt_1",
                                checkoutId = "chk_bt_1",
                                name = CheckoutTestFixtures.MERCHANT_NAME,
                                amount = 50000,
                                allowedPaymentMethods = listOf(CheckoutPaymentMethods.BANK_TRANSFER),
                                email = CheckoutTestFixtures.DEFAULT_CHECKOUT_EMAIL,
                            ),
                    )
                }
            }

            onNodeWithText(copyLabel).assertIsDisplayed().performClick()
            onNodeWithText(copiedToastText).assertIsDisplayed()
        }
}
