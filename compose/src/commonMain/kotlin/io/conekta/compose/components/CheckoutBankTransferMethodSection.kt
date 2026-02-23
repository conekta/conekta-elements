package io.conekta.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_bank_transfer_reference_message
import io.conekta.compose.generated.resources.checkout_method_bank_transfer
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckoutBankTransferMethodSection(onSelect: () -> Unit) {
    CheckoutSelectedMethodContainer {
        CheckoutMethodRow(
            methodLabel = stringResource(Res.string.checkout_method_bank_transfer),
            iconUrl = CDNResources.Icons.BANK_TRANSFER,
            selected = true,
            onClick = onSelect,
        )
        CheckoutMethodContent(topPadding = 18.dp) {
            CheckoutEmailReferenceRow(
                text = stringResource(Res.string.checkout_bank_transfer_reference_message),
            )
        }
    }
}

@Composable
fun CheckoutBankTransferMethodItem(
    selected: Boolean,
    onClick: () -> Unit,
) {
    CheckoutMethodRow(
        methodLabel = stringResource(Res.string.checkout_method_bank_transfer),
        iconUrl = CDNResources.Icons.BANK_TRANSFER,
        selected = selected,
        onClick = onClick,
    )
}
