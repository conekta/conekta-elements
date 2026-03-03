package io.conekta.compose.testfixtures

import io.conekta.elements.checkout.models.CheckoutCharge
import io.conekta.elements.checkout.models.CheckoutChargePaymentMethod
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.checkout.models.CurrencyCodes

internal object CheckoutTestFixtures {
    const val MERCHANT_NAME = "Demo Store"
    const val REFERENCE_20_A = "12345678901234567890"
    const val REFERENCE_20_B = "09876543210987654321"
    const val REFERENCE_20_C = "11112222333344445555"
    const val REFERENCE_20_D = "22223333444455556666"
    const val DEFAULT_CHECKOUT_EMAIL = "test@conekta.com"
}

internal fun checkoutResultFixture(
    orderId: String = "ord_test_1",
    checkoutId: String = "chk_test_1",
    name: String = CheckoutTestFixtures.MERCHANT_NAME,
    amount: Int = 12345,
    currency: String = CurrencyCodes.MXN,
    allowedPaymentMethods: List<String> = emptyList(),
    email: String = "",
): CheckoutResult =
    CheckoutResult(
        orderId = orderId,
        checkoutId = checkoutId,
        name = name,
        amount = amount,
        currency = currency,
        allowedPaymentMethods = allowedPaymentMethods,
        email = email,
    )

internal fun paymentMethodFixture(
    reference: String = "",
    clabe: String = "",
    productType: String = "",
    agreement: String = "",
    barcodeUrl: String = "",
    expiresAt: Long = 0L,
): CheckoutChargePaymentMethod =
    CheckoutChargePaymentMethod(
        reference = reference,
        clabe = clabe,
        productType = productType,
        agreement = agreement,
        barcodeUrl = barcodeUrl,
        expiresAt = expiresAt,
    )

internal fun orderResultFixture(
    orderId: String = "ord_test_1",
    paymentMethod: CheckoutChargePaymentMethod = CheckoutChargePaymentMethod(),
): CheckoutOrderResult =
    CheckoutOrderResult(
        orderId = orderId,
        charges =
            listOf(
                CheckoutCharge(
                    paymentMethod = paymentMethod,
                ),
            ),
    )
