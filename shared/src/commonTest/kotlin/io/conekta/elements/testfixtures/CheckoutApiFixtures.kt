package io.conekta.elements.testfixtures

import io.conekta.elements.checkout.models.CurrencyCodes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object CheckoutApiFixtures {
    const val CHECKOUT_NAME = "Demo Store"
    const val CHECKOUT_AMOUNT = 30000

    @OptIn(ExperimentalUuidApi::class)
    fun randomUuid(): String = Uuid.random().toString()

    fun checkoutRequestPayload(
        allowedPaymentMethods: List<String>,
        includeProviders: Boolean = false,
        id: String = randomUuid(),
    ): String {
        val allowedMethodsJson = allowedPaymentMethods.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
        val providersJson =
            if (includeProviders) {
                """
                [
                  {"id":"647f8b322a0818004a414694","name":"farmacias_del_ahorro","paymentMethod":"cash"},
                  {"id":"66df25a6af1debf142e80026","name":"bbva","paymentMethod":"cash"}
                ]
                """.trimIndent()
            } else {
                "[]"
            }

        return """
            {
              "id":"$id",
              "name":"$CHECKOUT_NAME",
              "amount":$CHECKOUT_AMOUNT,
              "allowedPaymentMethods":$allowedMethodsJson,
              "providers":$providersJson,
              "orderTemplate":{
                "currency":"${CurrencyCodes.MXN}",
                "lineItems":[{"name":"Apple test 3","quantity":1,"unitPrice":30000}],
                "taxLines":[{"description":"test","amount":2000}],
                "discountLines":[],
                "shippingLines":[]
              }
            }
            """.trimIndent()
    }

    fun legacyCheckoutOrderPayload(): String =
        """
        {
          "id":"ord_legacy",
          "amount":12000,
          "currency":"${CurrencyCodes.MXN}",
          "line_items":{"data":[{"name":"Aretes Tres Círculos Numerales","quantity":1,"unit_price":10000}]},
          "tax_lines":{"data":[{"description":"test","amount":2000}]},
          "checkout":{
            "id":"chk_legacy",
            "allowed_payment_methods":["card","cash","bank_transfer"]
          }
        }
        """.trimIndent()

    fun apiErrorPayload(
        type: String,
        message: String,
        messageToPurchaser: String,
    ): String =
        """
        {
          "object":"error",
          "type":"$type",
          "message":"$message",
          "message_to_purchaser":"$messageToPurchaser"
        }
        """.trimIndent()

    fun malformedPurchaserMessageErrorPayload(
        status: String = "unprocessable_entity",
        message: String = "Backend validation failed",
    ): String =
        """
        {
          "status": "$status",
          "message": "$message",
          "message_to_purchaser": { "unexpected": true }
        }
        """.trimIndent()

    fun detailsOnlyMalformedPurchaserMessageErrorPayload(
        code: String = "custom_error",
        detailMessage: String = "First detail message",
    ): String =
        """
        {
          "code": "$code",
          "details": [
            { "message": "$detailMessage" }
          ],
          "message_to_purchaser": { "unexpected": true }
        }
        """.trimIndent()

    fun nestedDetailsErrorPayload(nestedDetailMessage: String): String =
        """
        {
          "type":"parameter_validation_error",
          "message":"There was a runtime error.",
          "details":[
            {
              "type":"parameter_validation_error",
              "details":[
                {
                  "code":"conekta.errors.parameter_validation.charge.international_card_not_allowed",
                  "debug_message":"$nestedDetailMessage",
                  "message":"$nestedDetailMessage"
                }
              ]
            }
          ]
        }
        """.trimIndent()

    fun checkoutRequestMinimalPayload(
        id: String = randomUuid(),
        name: String = "My checkout",
        amount: Long = 15000,
    ): String =
        """
        {
          "id":"$id",
          "name":"$name",
          "amount":$amount
        }
        """.trimIndent()

    fun checkoutOrderWithDiscountAndShippingPayload(
        id: String = randomUuid(),
        amount: Long = 12000,
        currency: String = CurrencyCodes.MXN,
        checkoutId: String = randomUuid(),
        discountDescription: String = "Promo",
        discountAmount: Long = 500,
        shippingDescription: String = "Envio",
        shippingAmount: Long = 250,
    ): String =
        """
        {
          "id":"$id",
          "amount":$amount,
          "currency":"$currency",
          "discount_lines": {
            "data": [
              { "description": "$discountDescription", "amount": $discountAmount }
            ]
          },
          "shipping_lines": {
            "data": [
              { "description": "$shippingDescription", "amount": $shippingAmount }
            ]
          },
          "checkout":{
            "id":"$checkoutId",
            "allowed_payment_methods":["card"]
          }
        }
        """.trimIndent()

    fun createOrderRequestPayload(
        checkoutRequestId: String = randomUuid(),
        paymentMethod: String,
        tokenId: String? = null,
    ): String {
        val tokenIdField = tokenId?.let { ",\"tokenId\":\"$it\"" } ?: ""
        return """
            {
              "checkoutRequestId":"$checkoutRequestId",
              "paymentMethod":"$paymentMethod"$tokenIdField
            }
            """.trimIndent()
    }

    fun createOrderResponseWithNextActionPayload(
        id: String = "ord_created_1",
        status: String = "pending_payment",
    ): String =
        """
        {
          "id":"$id",
          "status":"$status",
          "nextAction":{
            "type":"redirect_to_url",
            "redirectToUrl":{
              "returnUrl":"myapp://checkout/return",
              "url":"https://pay.conekta.com/redirect"
            }
          },
          "urlRedirect":"https://pay.conekta.com/redirect",
          "charges":[
            {
              "amount":12000,
              "currency":"${CurrencyCodes.MXN}",
              "status":"pending_payment",
              "payment_method":{
                "type":"cash",
                "reference":"1234567890",
                "clabe":"012180001234567890",
                "barcodeUrl":"https://assets.conekta.com/barcode.png",
                "expiresAt":1700000000,
                "service_name":"OXXO",
                "store_name":"OXXO Centro",
                "provider":"conekta_cash",
                "agreement":"ABC123",
                "name":"Cash In",
                "product_type":"cash_in"
              }
            }
          ]
        }
        """.trimIndent()

    fun createOrderResponseMinimalPayload(id: String = "ord_created_2"): String =
        """
        {
          "id":"$id"
        }
        """.trimIndent()
}
