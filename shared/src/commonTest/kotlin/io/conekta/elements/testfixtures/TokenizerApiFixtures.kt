package io.conekta.elements.testfixtures

object TokenizerApiFixtures {
    fun tokenResponsePayload(
        id: String,
        livemode: Boolean = false,
        used: Boolean = false,
        objectType: String = "token",
    ): String =
        """{"id":"$id","livemode":$livemode,"used":$used,"object":"$objectType"}"""

    fun tokenErrorPayload(
        type: String,
        message: String,
        messageToPurchaser: String,
        objectType: String = "error",
    ): String =
        """
        {
          "object":"$objectType",
          "type":"$type",
          "message":"$message",
          "message_to_purchaser":"$messageToPurchaser"
        }
        """.trimIndent()

    fun cardDataPayload(
        cvc: String,
        expMonth: String,
        expYear: String,
        name: String,
        number: String,
    ): String = """{"cvc":"$cvc","exp_month":"$expMonth","exp_year":"$expYear","name":"$name","number":"$number"}"""
}
