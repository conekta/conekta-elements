package io.conekta.compose.components.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue

internal class CardFieldsState {
    var cardholderName by mutableStateOf(TextFieldValue(""))
    var cardNumber by mutableStateOf(TextFieldValue(""))
    var expiryDate by mutableStateOf(TextFieldValue(""))
    var cvv by mutableStateOf(TextFieldValue(""))
    var cardholderNameError by mutableStateOf(false)
    var cardNumberError by mutableStateOf(false)
    var expiryDateError by mutableStateOf(false)
    var cvvError by mutableStateOf(false)
    var cardholderNameErrorMsg by mutableStateOf<String?>(null)
    var cardNumberErrorMsg by mutableStateOf<String?>(null)
    var expiryDateErrorMsg by mutableStateOf<String?>(null)
    var cvvErrorMsg by mutableStateOf<String?>(null)
}
