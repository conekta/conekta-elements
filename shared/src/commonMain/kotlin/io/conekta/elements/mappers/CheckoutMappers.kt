package io.conekta.elements.mappers

import io.conekta.elements.models.Checkout
import io.conekta.elements.models.Provider
import io.conekta.elements.models.OrderTemplate
import io.conekta.elements.models.CustomerInfo
import io.conekta.elements.models.Plan
import io.conekta.elements.dtos.CheckoutDto
import io.conekta.elements.dtos.ProviderDto
import io.conekta.elements.dtos.OrderTemplateDto
import io.conekta.elements.dtos.CustomerInfoDto
import io.conekta.elements.dtos.PlanDto

internal fun Checkout.toDto(): CheckoutDto =
    CheckoutDto(
        id = id,
        entityId = entityId,
        companyId = companyId,
        name = name,
        amount = amount.toInt(),
        quantity = quantity,
        liveMode = liveMode,
        status = status,
        type = type,
        recurrent = recurrent,
        expiredAt = expiredAt.toInt(),
        startsAt = startsAt.toInt(),
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
        threeDs = threeDs?.name,
        maxFailedRetries = maxFailedRetries,
        failureUrl = failureUrl,
        successUrl = successUrl,
        plans = (plans ?: emptyList()).map { it.toDto() }.toTypedArray(),
    )

internal fun Provider.toDto(): ProviderDto =
    ProviderDto(
        id = id,
        name = name,
        paymentMethod = paymentMethod,
        haveAccount = haveAccount,
    )

internal fun OrderTemplate.toDto(): OrderTemplateDto =
    OrderTemplateDto(
        lineItems = lineItems.toTypedArray(),
        customerInfo = customerInfo?.toDto(),
        currency = currency,
        metadata = metadata?.toTypedArray(),
        shippingLines = shippingLines?.toTypedArray(),
        taxLines = taxLines?.toTypedArray(),
        discountLines = discountLines?.toTypedArray(),
        subtotal = subtotal?.toInt(),
    )

internal fun CustomerInfo.toDto(): CustomerInfoDto =
    CustomerInfoDto(
        corporate = corporate,
        customerFingerprint = customerFingerprint,
        customerId = customerId,
        email = email,
        name = name,
        phone = phone,
    )

internal fun Plan.toDto(): PlanDto =
    PlanDto(
        id = id,
        name = name,
        amount = amount.toInt(),
        currency = currency,
        interval = interval?.name ?: "",
        frequency = frequency,
        expiryCount = expiryCount,
        subscriptionStart = subscriptionStart.toInt(),
        subscriptionEnd = subscriptionEnd.toInt(),
        trialStart = trialStart.toInt(),
        trialEnd = trialEnd.toInt(),
        trialPeriodDays = trialPeriodDays,
        liveMode = liveMode,
        createdAt = createdAt.toInt(),
    )