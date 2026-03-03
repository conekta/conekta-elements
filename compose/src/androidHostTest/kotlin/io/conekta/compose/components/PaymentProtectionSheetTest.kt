package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.payment_protected
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaymentProtectionSheetTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun paymentProtectionSheetShowsProtectionTitleAndMerchantDescription() =
        runComposeUiTest {
            val merchant = "My Store"
            var protectedTitle = ""

            setContent {
                protectedTitle = stringResource(Res.string.payment_protected)
                ConektaTheme {
                    PaymentProtectionSheet(
                        merchantName = merchant,
                        onDismiss = {},
                    )
                }
            }

            onNodeWithText(protectedTitle).assertIsDisplayed()
            onNodeWithText(merchant, substring = true).assertIsDisplayed()
        }
}
