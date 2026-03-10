package io.conekta.elements.mappers

import io.conekta.elements.models.CreateOrderPayload
import io.conekta.elements.models.CustomerInfo
import io.conekta.elements.models.NextAction
import io.conekta.elements.models.OrderResponse
import io.conekta.elements.models.RedirectToUrl
import io.conekta.elements.dtos.CreateOrderPayloadDto
import io.conekta.elements.dtos.CustomerInfoDto
import io.conekta.elements.dtos.NextActionDto
import io.conekta.elements.dtos.OrderResponseDto
import io.conekta.elements.dtos.RedirectToUrlDto

internal fun OrderResponse.toDto(): OrderResponseDto =
    OrderResponseDto(
        id = id,
        reference = reference,
        status = status,
        urlRedirect = urlRedirect,
        charges = charges.toTypedArray(),
        metaData = metaData,
        nextAction = nextAction?.toDto(),
        paymentStatus = paymentStatus,
        errors = errors?.toTypedArray(),
    )

internal fun NextAction.toDto(): NextActionDto =
    NextActionDto(
        type = type,
        redirectToUrl = redirectToUrl.toDto(),
    )

internal fun RedirectToUrl.toDto(): RedirectToUrlDto =
    RedirectToUrlDto(
        returnUrl = returnUrl,
        url = url,
    )

fun CreateOrderPayloadDto.toModel(): CreateOrderPayload =
    CreateOrderPayload(
        checkoutRequestId = checkoutRequestId,
        paymentMethod = paymentMethod,
        fingerprint = fingerprint,
        customerInfo = customerInfo?.toModel(),
        shippingContact = shippingContact,
        paymentKey = paymentKey,
        paymentSourceId = paymentSourceId,
        fillPaymentFormTime = fillPaymentFormTime,
        checkoutAntifraudResponseID = checkoutAntifraudResponseID,
        savePaymentSource = savePaymentSource,
        threeDsMode = threeDsMode,
        returnUrl = returnUrl,
        planId = planId,
        splitPayment = splitPayment,
        originalOrderId = originalOrderId,
        amount = amount,
        splitPaymentStep = splitPaymentStep,
    )

fun CustomerInfoDto.toModel(): CustomerInfo =
    CustomerInfo(
        corporate = corporate,
        customerFingerprint = customerFingerprint,
        customerId = customerId,
        email = email,
        name = name,
        phone = phone,
    )
