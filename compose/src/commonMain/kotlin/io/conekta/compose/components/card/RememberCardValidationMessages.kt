package io.conekta.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.error_field_required
import io.conekta.compose.generated.resources.validation_card_min_length
import io.conekta.compose.generated.resources.validation_cvv_min_length
import io.conekta.compose.generated.resources.validation_expiry_year_invalid
import io.conekta.compose.generated.resources.validation_invalid_card
import io.conekta.elements.tokenizer.validators.ValidationMessages
import io.conekta.elements.utils.currentTwoDigitYear
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun rememberCardValidationMessages(): ValidationMessages {
    val requiredFieldMessage = stringResource(Res.string.error_field_required)
    val cardMinLengthMessage = stringResource(Res.string.validation_card_min_length)
    val invalidCardMessage = stringResource(Res.string.validation_invalid_card)
    val minimumYearLabel = currentTwoDigitYear().toString().padStart(2, '0')
    val expiryYearInvalidMessage = stringResource(Res.string.validation_expiry_year_invalid, minimumYearLabel)
    val cvvMinLengthMessage = stringResource(Res.string.validation_cvv_min_length)

    return remember(
        requiredFieldMessage,
        cardMinLengthMessage,
        invalidCardMessage,
        expiryYearInvalidMessage,
        cvvMinLengthMessage,
    ) {
        ValidationMessages(
            required = requiredFieldMessage,
            cardMinLength = cardMinLengthMessage,
            invalidCard = invalidCardMessage,
            expiryYearInvalid = expiryYearInvalidMessage,
            cvvMinLength = cvvMinLengthMessage,
        )
    }
}
