package io.conekta.compose.components.banktransfer

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_bank_transfer_reference_message
import io.conekta.compose.generated.resources.checkout_method_bank_transfer
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CheckoutBankTransferMethodSectionTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutBankTransferMethodItemCallsOnClick() =
        runComposeUiTest {
            var clicked = false
            var methodLabel = ""
            setContent {
                methodLabel = stringResource(Res.string.checkout_method_bank_transfer)
                ConektaTheme {
                    CheckoutBankTransferMethodItem(
                        selected = false,
                        onClick = { clicked = true },
                    )
                }
            }

            onNodeWithText(methodLabel).assertIsDisplayed().performClick()
            assertTrue(clicked)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkoutBankTransferMethodSectionDisplaysReferenceMessage() =
        runComposeUiTest {
            var referenceMessage = ""
            setContent {
                referenceMessage = stringResource(Res.string.checkout_bank_transfer_reference_message)
                ConektaTheme {
                    CheckoutBankTransferMethodSection(onSelect = {})
                }
            }

            onNodeWithText(referenceMessage).assertIsDisplayed()
        }
}
