package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckoutApiModelsTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun orderResponseDtoDeserializesRequiredFields() {
        val payload =
            """
            {
              "id":"ord_123",
              "amount":12000,
              "currency":"MXN",
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
        assertEquals("MXN", dto.currency)
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
              "currency":"MXN",
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
}
