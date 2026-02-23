package io.conekta.compose

import androidx.compose.ui.window.ComposeUIViewController
import io.conekta.compose.checkout.ConektaCheckout
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import platform.UIKit.UIViewController

@Suppress("ktlint:standard:function-naming")
fun ConektaCheckoutViewController(
    config: CheckoutConfig,
    onPaymentMethodSelected: (String) -> Unit,
    onError: (CheckoutError) -> Unit,
): UIViewController =
    ComposeUIViewController {
        ConektaCheckout(
            config = config,
            onPaymentMethodSelected = onPaymentMethodSelected,
            onError = onError,
        )
    }
