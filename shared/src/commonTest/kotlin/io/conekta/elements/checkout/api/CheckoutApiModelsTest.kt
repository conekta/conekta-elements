package io.conekta.elements.checkout.api

import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CurrencyCodes
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckoutApiModelsTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun checkoutRequestResponseDtoDeserializesRequiredFields() {
        val payload =
            """
            {
              "id":"0f3e251c-90b7-4846-9ecb-e48b447f25e4",
              "amount":30000,
              "allowedPaymentMethods":["Card","Apple","cash_in","bbva_cash_in"],
              "providers":[
                {"id":"647f8b322a0818004a414694","name":"farmacias_del_ahorro","paymentMethod":"cash"},
                {"id":"66df25a6af1debf142e80026","name":"bbva","paymentMethod":"cash"}
              ],
              "orderTemplate":{
                "currency":"${CurrencyCodes.MXN}",
                "lineItems":[
                  {"name":"Apple test 3","quantity":1,"unitPrice":30000}
                ],
                "taxLines":[],
                "discountLines":[],
                "shippingLines":[]
              }
            }
            """.trimIndent()

        val dto = json.decodeFromString(CheckoutRequestResponseDto.serializer(), payload)

        assertEquals("0f3e251c-90b7-4846-9ecb-e48b447f25e4", dto.id)
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
}
