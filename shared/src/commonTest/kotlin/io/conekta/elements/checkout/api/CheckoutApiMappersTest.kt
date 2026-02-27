package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.ProductTypes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckoutApiMappersTest {
    @Test
    fun checkoutRequestResponseToDomainNormalizesMethodsAndInfersProviderProductType() {
        val dto =
            CheckoutRequestResponseDto(
                id = "checkout-id",
                name = "Checkout name",
                amount = 5000,
                allowedPaymentMethods = listOf(" Card ", "BANK_TRANSFER", "cash", "cash_in"),
                providers =
                    listOf(
                        CheckoutProviderDto(
                            id = "p1",
                            name = "bbva",
                            paymentMethod = "Cash",
                            productType = "",
                        ),
                        CheckoutProviderDto(
                            id = "p2",
                            name = "datalogic",
                            paymentMethod = "Bank Transfer",
                            productType = "",
                        ),
                        CheckoutProviderDto(
                            id = "p3",
                            name = "custom",
                            paymentMethod = "card",
                            productType = "custom_type",
                        ),
                    ),
                orderTemplate =
                    CheckoutOrderTemplateDto(
                        currency = "MXN",
                        customerInfo = CheckoutCustomerInfoDto(email = "test@conekta.com"),
                    ),
            )

        val result = dto.toDomain()

        assertEquals(
            listOf(
                CheckoutPaymentMethods.CARD,
                CheckoutPaymentMethods.BANK_TRANSFER,
                CheckoutPaymentMethods.CASH,
                "cash_in",
            ),
            result.allowedPaymentMethods,
        )
        assertEquals(ProductTypes.BBVA_CASH_IN, result.providers[0].productType)
        assertEquals(ProductTypes.CASH_IN, result.providers[1].productType)
        assertEquals("custom_type", result.providers[2].productType)
        assertEquals(CheckoutPaymentMethods.BANK_TRANSFER, result.providers[1].paymentMethod)
        assertEquals("test@conekta.com", result.email)
    }

    @Test
    fun checkoutOrderResponseToDomainMapsNullableCollectionsAndDescriptions() {
        val dto =
            CheckoutOrderResponseDto(
                id = "ord-id",
                amount = 12000,
                currency = "MXN",
                checkout = CheckoutDetailsDto(id = "chk-id", allowedPaymentMethods = listOf("card")),
                lineItems = null,
                taxLines = null,
                discountLines =
                    CheckoutAmountCollectionDto(
                        data = listOf(CheckoutAmountLineDto(description = null, amount = 300)),
                    ),
                shippingLines =
                    CheckoutAmountCollectionDto(
                        data = listOf(CheckoutAmountLineDto(description = null, amount = 100)),
                    ),
            )

        val result = dto.toDomain()

        assertEquals("ord-id", result.orderId)
        assertEquals("chk-id", result.checkoutId)
        assertTrue(result.lineItems.isEmpty())
        assertTrue(result.taxLines.isEmpty())
        assertEquals("", result.discountLines.first().description)
        assertEquals("", result.shippingLines.first().description)
    }

    @Test
    fun createOrderResponseToDomainMapsNestedFields() {
        val dto =
            CreateOrderResponseDto(
                id = "ord-created",
                status = "pending_payment",
                nextAction =
                    CreateOrderNextActionDto(
                        redirectToUrl =
                            CreateOrderRedirectToUrlDto(
                                returnUrl = "myapp://return",
                                url = "https://pay.conekta.com/redirect",
                            ),
                        type = "redirect_to_url",
                    ),
                urlRedirect = "https://pay.conekta.com/redirect",
                charges =
                    listOf(
                        CreateOrderChargeDto(
                            amount = 12000,
                            currency = "MXN",
                            status = "pending_payment",
                            paymentMethod =
                                CreateOrderChargePaymentMethodDto(
                                    type = "cash",
                                    reference = "123456",
                                    clabe = "012180001234567890",
                                    barcodeUrl = "https://assets.conekta.com/barcode.png",
                                    expiresAt = 1700000000,
                                    serviceName = "OXXO",
                                    storeName = "OXXO Centro",
                                    provider = "conekta_cash",
                                    agreement = "ABC123",
                                    name = "Cash In",
                                    productType = "cash_in",
                                ),
                        ),
                    ),
            )

        val result = dto.toDomain()

        assertEquals("ord-created", result.orderId)
        assertEquals("pending_payment", result.status)
        assertEquals("redirect_to_url", result.nextAction?.type)
        assertEquals("myapp://return", result.nextAction?.redirectToUrl?.returnUrl)
        assertEquals(
            "cash",
            result.charges
                .first()
                .paymentMethod
                ?.type,
        )
        assertEquals(
            "cash_in",
            result.charges
                .first()
                .paymentMethod
                ?.productType,
        )
    }

    @Test
    fun createOrderResponseToDomainUsesEmptyDefaultsForNullNestedFields() {
        val dto =
            CreateOrderResponseDto(
                id = "ord-created",
                urlRedirect = null,
                nextAction =
                    CreateOrderNextActionDto(
                        redirectToUrl = CreateOrderRedirectToUrlDto(returnUrl = null, url = null),
                        type = null,
                    ),
                charges =
                    listOf(
                        CreateOrderChargeDto(
                            paymentMethod = CreateOrderChargePaymentMethodDto(),
                        ),
                    ),
            )

        val result = dto.toDomain()

        assertEquals("", result.urlRedirect)
        assertEquals("", result.nextAction?.type)
        assertEquals("", result.nextAction?.redirectToUrl?.returnUrl)
        assertEquals("", result.nextAction?.redirectToUrl?.url)
        assertEquals(
            "",
            result.charges
                .first()
                .paymentMethod
                ?.type,
        )
        assertEquals(
            "",
            result.charges
                .first()
                .paymentMethod
                ?.reference,
        )
        assertEquals(
            "",
            result.charges
                .first()
                .paymentMethod
                ?.productType,
        )
    }
}
