package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutAmountLine
import io.conekta.elements.checkout.models.CheckoutCharge
import io.conekta.elements.checkout.models.CheckoutChargePaymentMethod
import io.conekta.elements.checkout.models.CheckoutLineItem
import io.conekta.elements.checkout.models.CheckoutNextAction
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutProvider
import io.conekta.elements.checkout.models.CheckoutRedirectToUrl
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.checkout.models.ProductTypes

internal fun CreateOrderResponseDto.toDomain(): CheckoutOrderResult =
    CheckoutOrderResult(
        orderId = id,
        status = status,
        nextAction =
            nextAction?.let {
                CheckoutNextAction(
                    redirectToUrl =
                        it.redirectToUrl?.let { redirect ->
                            CheckoutRedirectToUrl(
                                returnUrl = redirect.returnUrl.orEmpty(),
                                url = redirect.url.orEmpty(),
                            )
                        },
                    type = it.type.orEmpty(),
                )
            },
        urlRedirect = urlRedirect.orEmpty(),
        charges = charges.map { it.toDomain() },
    )

private fun CreateOrderChargeDto.toDomain(): CheckoutCharge =
    CheckoutCharge(
        amount = amount.toInt(),
        currency = currency,
        status = status,
        paymentMethod = paymentMethod?.toDomain(),
    )

private fun CreateOrderChargePaymentMethodDto.toDomain(): CheckoutChargePaymentMethod =
    CheckoutChargePaymentMethod(
        type = type.orEmpty(),
        reference = reference.orEmpty(),
        clabe = clabe.orEmpty(),
        barcodeUrl = barcodeUrl.orEmpty(),
        expiresAt = expiresAt,
        serviceName = serviceName.orEmpty(),
        storeName = storeName.orEmpty(),
        provider = provider.orEmpty(),
        agreement = agreement.orEmpty(),
        name = name.orEmpty(),
        productType = productType.orEmpty(),
    )

internal fun CheckoutRequestResponseDto.toDomain(): CheckoutResult =
    CheckoutResult(
        orderId = id,
        checkoutId = id,
        name = name,
        amount = amount.toInt(),
        currency = orderTemplate.currency,
        allowedPaymentMethods = allowedPaymentMethods.map(::normalizePaymentMethodValue),
        providers =
            providers.map {
                CheckoutProvider(
                    id = it.id,
                    name = it.name,
                    paymentMethod = normalizePaymentMethodValue(it.paymentMethod),
                    productType = it.productType.ifBlank { inferProductTypeFromProviderName(it.name) },
                )
            },
        lineItems =
            orderTemplate.lineItems.map {
                CheckoutLineItem(
                    name = it.name,
                    quantity = it.quantity,
                    unitPrice = it.unitPrice,
                )
            },
        taxLines =
            orderTemplate.taxLines.map {
                CheckoutAmountLine(
                    description = it.description.orEmpty(),
                    amount = it.amount,
                )
            },
        discountLines =
            orderTemplate.discountLines.map {
                CheckoutAmountLine(
                    description = it.description.orEmpty(),
                    amount = it.amount,
                )
            },
        shippingLines =
            orderTemplate.shippingLines.map {
                CheckoutAmountLine(
                    description = it.description.orEmpty(),
                    amount = it.amount,
                )
            },
        email = orderTemplate.customerInfo?.email.orEmpty(),
    )

internal fun CheckoutOrderResponseDto.toDomain(): CheckoutResult =
    CheckoutResult(
        orderId = id,
        checkoutId = checkout.id,
        name = "",
        amount = amount.toInt(),
        currency = currency,
        allowedPaymentMethods = checkout.allowedPaymentMethods.map(::normalizePaymentMethodValue),
        providers = emptyList(),
        lineItems =
            lineItems?.data.orEmpty().map {
                CheckoutLineItem(
                    name = it.name,
                    quantity = it.quantity,
                    unitPrice = it.unitPrice,
                )
            },
        taxLines =
            taxLines?.data.orEmpty().map {
                CheckoutAmountLine(
                    description = it.description.orEmpty(),
                    amount = it.amount,
                )
            },
        discountLines =
            discountLines?.data.orEmpty().map {
                CheckoutAmountLine(
                    description = it.description.orEmpty(),
                    amount = it.amount,
                )
            },
        shippingLines =
            shippingLines?.data.orEmpty().map {
                CheckoutAmountLine(
                    description = it.description.orEmpty(),
                    amount = it.amount,
                )
            },
    )

private fun normalizePaymentMethodValue(value: String): String {
    val compact =
        value
            .trim()
            .replace(" ", "")
            .replace("_", "")
            .lowercase()

    return when (compact) {
        "card" -> CheckoutPaymentMethods.CARD
        "cash" -> CheckoutPaymentMethods.CASH
        "banktransfer" -> CheckoutPaymentMethods.BANK_TRANSFER
        else -> value.trim().lowercase()
    }
}

private fun inferProductTypeFromProviderName(name: String): String =
    when (name.trim().lowercase()) {
        "bbva" -> ProductTypes.BBVA_CASH_IN
        "datalogic" -> ProductTypes.CASH_IN
        else -> ""
    }
