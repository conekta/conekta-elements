package io.conekta.compose.components.banktransfer

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.success_bank_transfer_clabe_format
import io.conekta.compose.generated.resources.success_bank_transfer_copy_number
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.testfixtures.CheckoutTestFixtures
import io.conekta.compose.theme.ConektaTheme
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class BankTransferSuccessReferenceCardTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun bankTransferSuccessReferenceCardDisplaysAmountAndClabe() =
        runComposeUiTest {
            val clabeReference = CheckoutTestFixtures.REFERENCE_20_A
            var formattedClabe = ""
            var copyLabel = ""

            setContent {
                formattedClabe = stringResource(Res.string.success_bank_transfer_clabe_format, clabeReference)
                copyLabel = stringResource(Res.string.success_bank_transfer_copy_number)
                ConektaTheme {
                    BankTransferSuccessReferenceCard(
                        amountText = "\$120.00",
                        clabeReference = clabeReference,
                        expiresAt = 0L,
                        onCopyClick = {},
                    )
                }
            }

            onNodeWithText("\$120.00").assertIsDisplayed()
            onNodeWithText(formattedClabe).assertIsDisplayed()
            onNodeWithText(copyLabel).assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun bankTransferSuccessReferenceCardCallsOnCopyClickWhenReferenceIsNotBlank() =
        runComposeUiTest {
            var copied = false
            var copyLabel = ""
            setContent {
                copyLabel = stringResource(Res.string.success_bank_transfer_copy_number)
                ConektaTheme {
                    BankTransferSuccessReferenceCard(
                        amountText = "\$120.00",
                        clabeReference = CheckoutTestFixtures.REFERENCE_20_A,
                        expiresAt = 0L,
                        onCopyClick = { copied = true },
                    )
                }
            }

            onNodeWithText(copyLabel).performClick()
            assertTrue(copied)
        }
}
