package io.conekta.elements.mappers

import io.conekta.elements.models.Checkout
import io.conekta.elements.dtos.*

internal fun Checkout.toDto(): CheckoutDto =
    CheckoutDto(
        id = id,
        entityId = entityId,
        companyId = companyId,
        name = name,
        amount = amount,
        quantity = quantity,
        liveMode = liveMode,
        status = status,
        type = type,
        recurrent = recurrent,
        expiredAt = expiredAt,
        startsAt = startsAt,
        allowedPaymentMethods = allowedPaymentMethods.toTypedArray(),
        slug = slug,
        url = url,
        needsShippingContact = needsShippingContact,
        orderTemplate = orderTemplate.toDto(),
        monthlyInstallmentsEnabled = monthlyInstallmentsEnabled,
        monthlyInstallmentsOptions = monthlyInstallmentsOptions.toTypedArray(),
        force3dsFlow = force3dsFlow,
        excludeCardNetworks = excludeCardNetworks.toTypedArray(),
        canNotExpire = canNotExpire,
        redirectionTime = redirectionTime,
        providers = providers.map { it.toDto() }.toTypedArray(),
        femsaMigrated = femsaMigrated,
        threeDs = threeDs?.name, // o mapping mejor si exportas enum
        maxFailedRetries = maxFailedRetries,
        failureUrl = failureUrl,
        successUrl = successUrl,
    )

internal fun io.conekta.elements.models.Provider.toDto(): ProviderDto =
    ProviderDto(
        id = id,
        name = name,
        paymentMethod = paymentMethod,
        haveAccount = haveAccount,
    )

internal fun io.conekta.elements.models.OrderTemplate.toDto(): OrderTemplateDto =
    OrderTemplateDto(
        lineItems = lineItems.toTypedArray(),
        customerInfo = customerInfo?.toDto(),
        currency = currency,
        metadata = metadata?.toTypedArray(),
        shippingLines = shippingLines?.toTypedArray(),
        taxLines = taxLines?.toTypedArray(),
        discountLines = discountLines?.toTypedArray(),
        subtotal = subtotal,
    )

internal fun io.conekta.elements.models.CustomerInfo.toDto(): CustomerInfoDto =
    CustomerInfoDto(
        corporate = corporate,
        customerFingerprint = customerFingerprint,
        customerId = customerId,
        email = email,
        name = name,
        phone = phone,
    )