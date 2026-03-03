package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CurrencyCodes
import io.conekta.elements.testfixtures.CheckoutApiFixtures
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CheckoutApiModelsTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun checkoutRequestResponseDtoDeserializesRequiredFields() {
        val expectedCheckoutId = CheckoutApiFixtures.randomUuid()
        val payload =
            CheckoutApiFixtures.checkoutRequestPayload(
                allowedPaymentMethods = listOf("Card", "Apple", "cash_in", "bbva_cash_in"),
                includeProviders = true,
                id = expectedCheckoutId,
            )

        val dto = json.decodeFromString(CheckoutRequestResponseDto.serializer(), payload)

        assertEquals(expectedCheckoutId, dto.id)
        assertEquals(CheckoutApiFixtures.CHECKOUT_NAME, dto.name)
        assertEquals(30000L, dto.amount)
        assertEquals(listOf("Card", "Apple", "cash_in", "bbva_cash_in"), dto.allowedPaymentMethods)
        assertEquals(2, dto.providers.size)
        assertEquals("farmacias_del_ahorro", dto.providers.first().name)
        assertEquals(CurrencyCodes.MXN, dto.orderTemplate.currency)
        assertEquals(1, dto.orderTemplate.lineItems.size)
        assertEquals(
            "Apple test 3",
            dto.orderTemplate
                .lineItems
                .first()
                .name,
        )
        assertEquals(
            30000L,
            dto.orderTemplate
                .lineItems
                .first()
                .unitPrice,
        )
    }

    @Test
    fun orderResponseDtoDeserializesRequiredFields() {
        val payload =
            """
            {
              "id":"ord_123",
              "amount":12000,
              "currency":"${CurrencyCodes.MXN}",
              "line_items": {
                "data": [
                  { "name": "Aretes", "quantity": 1, "unit_price": 10000 }
                ]
              },
              "tax_lines": {
                "data": [
                  { "description": "IVA", "amount": 2000 }
                ]
              },
              "checkout":{
                "id":"chk_123",
                "allowed_payment_methods":["card","cash","bank_transfer"]
              }
            }
            """.trimIndent()

        val dto = json.decodeFromString(CheckoutOrderResponseDto.serializer(), payload)

        assertEquals("ord_123", dto.id)
        assertEquals(12000L, dto.amount)
        assertEquals(CurrencyCodes.MXN, dto.currency)
        assertEquals("chk_123", dto.checkout.id)
        assertEquals(
            listOf(
                CheckoutPaymentMethods.CARD,
                CheckoutPaymentMethods.CASH,
                CheckoutPaymentMethods.BANK_TRANSFER,
            ),
            dto.checkout.allowedPaymentMethods,
        )
        assertEquals(1, dto.lineItems?.data?.size)
        assertEquals(
            "Aretes",
            dto.lineItems
                ?.data
                ?.first()
                ?.name,
        )
        assertEquals(
            1,
            dto.lineItems
                ?.data
                ?.first()
                ?.quantity,
        )
        assertEquals(
            10000L,
            dto.lineItems
                ?.data
                ?.first()
                ?.unitPrice,
        )
        assertEquals(1, dto.taxLines?.data?.size)
        assertEquals(
            "IVA",
            dto.taxLines
                ?.data
                ?.first()
                ?.description,
        )
        assertEquals(
            2000L,
            dto.taxLines
                ?.data
                ?.first()
                ?.amount,
        )
    }

    @Test
    fun orderResponseDtoIgnoresUnknownFields() {
        val payload =
            """
            {
              "id":"ord_123",
              "amount":12000,
              "currency":"${CurrencyCodes.MXN}",
              "extra":"value",
              "checkout":{
                "id":"chk_123",
                "allowed_payment_methods":["card"],
                "unused":true
              }
            }
            """.trimIndent()

        val dto = json.decodeFromString(CheckoutOrderResponseDto.serializer(), payload)
        assertEquals("ord_123", dto.id)
        assertEquals(listOf(CheckoutPaymentMethods.CARD), dto.checkout.allowedPaymentMethods)
    }

    @Test
    fun errorResponseDtoDeserializesSnakeCaseMessage() {
        val payload =
            """
            {
              "object":"error",
              "type":"invalid_request_error",
              "message":"bad request",
              "message_to_purchaser":"No fue posible procesar"
            }
            """.trimIndent()

        val dto = json.decodeFromString(CheckoutErrorResponseDto.serializer(), payload)
        assertEquals("error", dto.objectType)
        assertEquals("invalid_request_error", dto.type)
        assertEquals("bad request", dto.message)
        assertEquals("No fue posible procesar", dto.messageToPurchaser)
    }

    @Test
    fun errorResponseDtoDefaultsWhenEmpty() {
        val dto = json.decodeFromString(CheckoutErrorResponseDto.serializer(), "{}")
        assertEquals("error", dto.objectType)
        assertEquals("", dto.type)
        assertEquals("", dto.message)
        assertEquals("", dto.messageToPurchaser)
        assertTrue(dto.messageToPurchaser.isEmpty())
    }

    @Test
    fun checkoutRequestResponseDtoDefaultsOptionalFieldsWhenMissing() {
        val expectedCheckoutId = CheckoutApiFixtures.randomUuid()
        val payload = CheckoutApiFixtures.checkoutRequestMinimalPayload(id = expectedCheckoutId)

        val dto = json.decodeFromString(CheckoutRequestResponseDto.serializer(), payload)

        assertEquals(expectedCheckoutId, dto.id)
        assertEquals("My checkout", dto.name)
        assertEquals(15000L, dto.amount)
        assertNull(dto.status)
        assertNull(dto.startsAt)
        assertTrue(dto.allowedPaymentMethods.isEmpty())
        assertTrue(dto.providers.isEmpty())
        assertEquals(CurrencyCodes.MXN, dto.orderTemplate.currency)
        assertTrue(dto.orderTemplate.lineItems.isEmpty())
        assertTrue(dto.orderTemplate.taxLines.isEmpty())
        assertTrue(dto.orderTemplate.discountLines.isEmpty())
        assertTrue(dto.orderTemplate.shippingLines.isEmpty())
        assertNull(dto.orderTemplate.customerInfo)
    }

    @Test
    fun checkoutOrderResponseDtoDeserializesDiscountAndShippingCollections() {
        val expectedCheckoutId = CheckoutApiFixtures.randomUuid()
        val payload = CheckoutApiFixtures.checkoutOrderWithDiscountAndShippingPayload(checkoutId = expectedCheckoutId)

        val dto = json.decodeFromString(CheckoutOrderResponseDto.serializer(), payload)

        assertEquals(expectedCheckoutId, dto.checkout.id)
        assertEquals(1, dto.discountLines?.data?.size)
        assertEquals(
            "Promo",
            dto.discountLines
                ?.data
                ?.first()
                ?.description,
        )
        assertEquals(
            500L,
            dto.discountLines
                ?.data
                ?.first()
                ?.amount,
        )
        assertEquals(1, dto.shippingLines?.data?.size)
        assertEquals(
            "Envio",
            dto.shippingLines
                ?.data
                ?.first()
                ?.description,
        )
        assertEquals(
            250L,
            dto.shippingLines
                ?.data
                ?.first()
                ?.amount,
        )
    }

    @Test
    fun createOrderRequestDtoDeserializesNullableTokenId() {
        val expectedCheckoutRequestId = CheckoutApiFixtures.randomUuid()
        val withToken =
            CheckoutApiFixtures.createOrderRequestPayload(
                checkoutRequestId = expectedCheckoutRequestId,
                paymentMethod = "card",
                tokenId = "tok_123",
            )
        val withoutToken = CheckoutApiFixtures.createOrderRequestPayload(paymentMethod = "cash")

        val dtoWithToken = json.decodeFromString(CreateOrderRequestDto.serializer(), withToken)
        val dtoWithoutToken = json.decodeFromString(CreateOrderRequestDto.serializer(), withoutToken)

        assertEquals(expectedCheckoutRequestId, dtoWithToken.checkoutRequestId)
        assertEquals("card", dtoWithToken.paymentMethod)
        assertEquals("tok_123", dtoWithToken.tokenId)
        assertEquals("cash", dtoWithoutToken.paymentMethod)
        assertNull(dtoWithoutToken.tokenId)
    }

    @Test
    fun createOrderResponseDtoDeserializesNestedNextActionAndChargePaymentMethod() {
        val payload = CheckoutApiFixtures.createOrderResponseWithNextActionPayload()

        val dto = json.decodeFromString(CreateOrderResponseDto.serializer(), payload)

        assertEquals("ord_created_1", dto.id)
        assertEquals("pending_payment", dto.status)
        assertEquals("https://pay.conekta.com/redirect", dto.urlRedirect)
        assertEquals("redirect_to_url", dto.nextAction?.type)
        assertEquals("myapp://checkout/return", dto.nextAction?.redirectToUrl?.returnUrl)
        assertEquals("https://pay.conekta.com/redirect", dto.nextAction?.redirectToUrl?.url)
        assertEquals(1, dto.charges.size)
        assertEquals(12000L, dto.charges.first().amount)
        assertEquals(
            "cash",
            dto.charges
                .first()
                .paymentMethod
                ?.type,
        )
        assertEquals(
            "1234567890",
            dto.charges
                .first()
                .paymentMethod
                ?.reference,
        )
        assertEquals(
            "cash_in",
            dto.charges
                .first()
                .paymentMethod
                ?.productType,
        )
    }

    @Test
    fun createOrderResponseDtoDefaultsWhenOptionalFieldsAreMissing() {
        val payload = CheckoutApiFixtures.createOrderResponseMinimalPayload()

        val dto = json.decodeFromString(CreateOrderResponseDto.serializer(), payload)

        assertEquals("ord_created_2", dto.id)
        assertEquals("", dto.status)
        assertNull(dto.nextAction)
        assertNull(dto.urlRedirect)
        assertTrue(dto.charges.isEmpty())
    }
}
