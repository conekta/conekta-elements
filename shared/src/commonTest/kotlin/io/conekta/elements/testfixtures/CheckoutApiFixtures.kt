package io.conekta.elements.testfixtures

import io.conekta.elements.checkout.models.CurrencyCodes

object CheckoutApiFixtures {
    const val CHECKOUT_ID = "0f3e251c-90b7-4846-9ecb-e48b447f25e4"
    const val CHECKOUT_REQUEST_ID = "dc5baf10-0f2b-4378-9f74-afa6bb418198"
    const val CHECKOUT_NAME = "Demo Store"
    const val CHECKOUT_AMOUNT = 30000

    fun checkoutRequestPayload(
        allowedPaymentMethods: List<String>,
        includeProviders: Boolean = false,
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
              "id":"$CHECKOUT_ID",
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
}
