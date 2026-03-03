package io.conekta.compose.components.card

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.error_field_required
import io.conekta.compose.generated.resources.validation_card_min_length
import io.conekta.compose.generated.resources.validation_cvv_min_length
import io.conekta.compose.generated.resources.validation_expiry_year_invalid
import io.conekta.compose.generated.resources.validation_invalid_card
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import io.conekta.elements.tokenizer.validators.ValidationMessages
import io.conekta.elements.utils.currentTwoDigitYear
import org.jetbrains.compose.resources.stringResource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class RememberCardValidationMessagesTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun rememberCardValidationMessagesUsesLocalizedStrings() =
        runComposeUiTest {
            var actual: ValidationMessages? = null
            var requiredMessage = ""
            var cardMinLengthMessage = ""
            var invalidCardMessage = ""
            var expiryYearInvalidMessage = ""
            var cvvMinLengthMessage = ""

            setContent {
                val minimumYearLabel = currentTwoDigitYear().toString().padStart(2, '0')
                requiredMessage = stringResource(Res.string.error_field_required)
                cardMinLengthMessage = stringResource(Res.string.validation_card_min_length)
                invalidCardMessage = stringResource(Res.string.validation_invalid_card)
                expiryYearInvalidMessage = stringResource(Res.string.validation_expiry_year_invalid, minimumYearLabel)
                cvvMinLengthMessage = stringResource(Res.string.validation_cvv_min_length)
                ConektaTheme {
                    actual = rememberCardValidationMessages()
                }
            }

            val resolved = checkNotNull(actual)
            assertEquals(requiredMessage, resolved.required)
            assertEquals(cardMinLengthMessage, resolved.cardMinLength)
            assertEquals(invalidCardMessage, resolved.invalidCard)
            assertEquals(expiryYearInvalidMessage, resolved.expiryYearInvalid)
            assertEquals(cvvMinLengthMessage, resolved.cvvMinLength)
        }
}
