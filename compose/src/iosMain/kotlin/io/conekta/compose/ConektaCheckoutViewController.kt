package io.conekta.compose

import androidx.compose.ui.window.ComposeUIViewController
import io.conekta.compose.checkout.ConektaCheckout
import io.conekta.elements.checkout.api.CheckoutApiService
import io.conekta.elements.checkout.models.CheckoutAmountLine
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutLineItem
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutResult
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

@Suppress("ktlint:standard:function-naming")
fun ConektaCheckoutMockViewController(
    config: CheckoutConfig,
    onPaymentMethodSelected: (String) -> Unit,
    onError: (CheckoutError) -> Unit,
): UIViewController =
    ComposeUIViewController {
        ConektaCheckout(
            config = config,
            onPaymentMethodSelected = onPaymentMethodSelected,
            onError = onError,
            checkoutApiServiceFactory = { checkoutConfig ->
                object : CheckoutApiService(checkoutConfig) {
                    override suspend fun fetchCheckout(): Result<CheckoutResult> =
                        Result.success(
                            CheckoutResult(
                                orderId = "ord_2zb4KeLHjraBbRJgs",
                                checkoutId = "dc5baf10-0f2b-4378-9f74-afa6bb418198",
                                amount = 12000,
                                currency = "MXN",
                                allowedPaymentMethods =
                                    listOf(
                                        CheckoutPaymentMethods.CARD,
                                        "bnpl",
                                        CheckoutPaymentMethods.CASH,
                                        "pay_by_bank",
                                        CheckoutPaymentMethods.BANK_TRANSFER,
                                        "apple",
                                    ),
                                lineItems =
                                    listOf(
                                        CheckoutLineItem(
                                            name = "Aretes Tres Círculos Numerales",
                                            quantity = 1,
                                            unitPrice = 10000,
                                        ),
                                    ),
                                taxLines =
                                    listOf(
                                        CheckoutAmountLine(
                                            description = "Test",
                                            amount = 2000,
                                        ),
                                    ),
                            ),
                        )
                }
            },
        )
    }
