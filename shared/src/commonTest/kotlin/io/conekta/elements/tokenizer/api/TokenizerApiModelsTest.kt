package io.conekta.elements.tokenizer.api

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TokenizerApiModelsTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun cardDataDtoSerializesToSnakeCase() {
        val dto =
            CardDataDto(
                cvc = "123",
                expMonth = "12",
                expYear = "26",
                name = "John Doe",
                number = "4242424242424242",
            )
        val serialized = json.encodeToString(CardDataDto.serializer(), dto)
        assertTrue(serialized.contains("\"exp_month\":\"12\""), "Expected snake_case exp_month, got: $serialized")
        assertTrue(serialized.contains("\"exp_year\":\"26\""), "Expected snake_case exp_year, got: $serialized")
    }

    @Test
    fun cardDataDtoDeserializesFromSnakeCase() {
        val jsonStr = """{"cvc":"456","exp_month":"01","exp_year":"28","name":"Jane","number":"5555555555554444"}"""
        val dto = json.decodeFromString(CardDataDto.serializer(), jsonStr)
        assertEquals("456", dto.cvc)
        assertEquals("01", dto.expMonth)
        assertEquals("28", dto.expYear)
        assertEquals("Jane", dto.name)
        assertEquals("5555555555554444", dto.number)
    }

    @Test
    fun cardPayloadDtoWrapsCardData() {
        val payload =
            CardPayloadDto(
                card = CardDataDto("123", "12", "26", "Test", "4111111111111111"),
            )
        val serialized = json.encodeToString(CardPayloadDto.serializer(), payload)
        assertTrue(serialized.contains("\"card\":{"), "Expected nested card object, got: $serialized")
    }

    @Test
    fun tokenRequestDtoSerialization() {
        val req = TokenRequestDto(data = "encrypted_data", key = "encrypted_key")
        val serialized = json.encodeToString(TokenRequestDto.serializer(), req)
        assertEquals("""{"data":"encrypted_data","key":"encrypted_key"}""", serialized)
    }

    @Test
    fun tokenResponseDtoDeserialization() {
        val jsonStr = """{"id":"tok_abc123","livemode":false,"used":false,"object":"token"}"""
        val dto = json.decodeFromString(TokenResponseDto.serializer(), jsonStr)
        assertEquals("tok_abc123", dto.id)
        assertFalse(dto.livemode)
        assertFalse(dto.used)
        assertEquals("token", dto.objectType)
    }

    @Test
    fun tokenResponseDtoIgnoresUnknownKeys() {
        val jsonStr = """{"id":"tok_xyz","livemode":true,"used":true,"object":"token","extra_field":"ignored"}"""
        val dto = json.decodeFromString(TokenResponseDto.serializer(), jsonStr)
        assertEquals("tok_xyz", dto.id)
    }

    @Test
    fun tokenErrorResponseDtoDeserialization() {
        val jsonStr = """{
            "object":"error",
            "type":"invalid_request_error",
            "message":"card number is invalid",
            "message_to_purchaser":"The card could not be processed"
        }"""
        val dto = json.decodeFromString(TokenErrorResponseDto.serializer(), jsonStr)
        assertEquals("error", dto.objectType)
        assertEquals("invalid_request_error", dto.type)
        assertEquals("card number is invalid", dto.message)
        assertEquals("The card could not be processed", dto.messageToPurchaser)
    }

    @Test
    fun tokenErrorResponseDtoDefaultValues() {
        val jsonStr = """{}"""
        val dto = json.decodeFromString(TokenErrorResponseDto.serializer(), jsonStr)
        assertEquals("error", dto.objectType)
        assertEquals("", dto.type)
        assertEquals("", dto.message)
        assertEquals("", dto.messageToPurchaser)
    }

    // --- Coverage for serialization default-value branches ---

    @Test
    fun tokenResponseDtoDeserializesWithOnlyId() {
        val dto = json.decodeFromString(TokenResponseDto.serializer(), """{"id":"tok_min"}""")
        assertEquals("tok_min", dto.id)
        assertFalse(dto.livemode)
        assertFalse(dto.used)
        assertEquals("", dto.objectType)
    }

    @Test
    fun tokenResponseDtoDeserializesWithLivemodeOnly() {
        val dto = json.decodeFromString(TokenResponseDto.serializer(), """{"id":"tok_1","livemode":true}""")
        assertTrue(dto.livemode)
        assertFalse(dto.used)
        assertEquals("", dto.objectType)
    }

    @Test
    fun tokenResponseDtoDeserializesWithUsedOnly() {
        val dto = json.decodeFromString(TokenResponseDto.serializer(), """{"id":"tok_2","used":true}""")
        assertFalse(dto.livemode)
        assertTrue(dto.used)
        assertEquals("", dto.objectType)
    }

    @Test
    fun tokenResponseDtoDeserializesWithObjectOnly() {
        val dto = json.decodeFromString(TokenResponseDto.serializer(), """{"id":"tok_3","object":"token"}""")
        assertFalse(dto.livemode)
        assertFalse(dto.used)
        assertEquals("token", dto.objectType)
    }

    @Test
    fun tokenResponseDtoRoundTrip() {
        val original = TokenResponseDto(id = "tok_rt", livemode = true, used = true, objectType = "token")
        val serialized = json.encodeToString(TokenResponseDto.serializer(), original)
        val deserialized = json.decodeFromString(TokenResponseDto.serializer(), serialized)
        assertEquals(original, deserialized)
    }

    @Test
    fun tokenErrorResponseDtoWithOnlyType() {
        val dto = json.decodeFromString(TokenErrorResponseDto.serializer(), """{"type":"validation_error"}""")
        assertEquals("error", dto.objectType)
        assertEquals("validation_error", dto.type)
        assertEquals("", dto.message)
        assertEquals("", dto.messageToPurchaser)
    }

    @Test
    fun tokenErrorResponseDtoWithOnlyMessage() {
        val dto = json.decodeFromString(TokenErrorResponseDto.serializer(), """{"message":"bad card"}""")
        assertEquals("error", dto.objectType)
        assertEquals("", dto.type)
        assertEquals("bad card", dto.message)
        assertEquals("", dto.messageToPurchaser)
    }

    @Test
    fun tokenErrorResponseDtoWithOnlyPurchaserMessage() {
        val dto =
            json.decodeFromString(
                TokenErrorResponseDto.serializer(),
                """{"message_to_purchaser":"Card declined"}""",
            )
        assertEquals("error", dto.objectType)
        assertEquals("", dto.type)
        assertEquals("", dto.message)
        assertEquals("Card declined", dto.messageToPurchaser)
    }

    @Test
    fun tokenErrorResponseDtoWithCustomObject() {
        val dto = json.decodeFromString(TokenErrorResponseDto.serializer(), """{"object":"custom_error"}""")
        assertEquals("custom_error", dto.objectType)
        assertEquals("", dto.type)
        assertEquals("", dto.message)
        assertEquals("", dto.messageToPurchaser)
    }

    @Test
    fun tokenErrorResponseDtoRoundTrip() {
        val original =
            TokenErrorResponseDto(
                objectType = "error",
                type = "api_error",
                message = "Internal error",
                messageToPurchaser = "Try again",
            )
        val serialized = json.encodeToString(TokenErrorResponseDto.serializer(), original)
        val deserialized = json.decodeFromString(TokenErrorResponseDto.serializer(), serialized)
        assertEquals(original, deserialized)
    }

    @Test
    fun tokenResponseDtoDeserializesWithLivemodeAndUsed() {
        val dto =
            json.decodeFromString(
                TokenResponseDto.serializer(),
                """{"id":"tok_lu","livemode":true,"used":true}""",
            )
        assertTrue(dto.livemode)
        assertTrue(dto.used)
        assertEquals("", dto.objectType)
    }

    @Test
    fun tokenResponseDtoDeserializesWithUsedAndObject() {
        val dto =
            json.decodeFromString(
                TokenResponseDto.serializer(),
                """{"id":"tok_uo","used":true,"object":"token"}""",
            )
        assertFalse(dto.livemode)
        assertTrue(dto.used)
        assertEquals("token", dto.objectType)
    }

    @Test
    fun tokenErrorResponseDtoWithTypeAndMessage() {
        val dto =
            json.decodeFromString(
                TokenErrorResponseDto.serializer(),
                """{"type":"invalid_request_error","message":"bad number"}""",
            )
        assertEquals("error", dto.objectType)
        assertEquals("invalid_request_error", dto.type)
        assertEquals("bad number", dto.message)
        assertEquals("", dto.messageToPurchaser)
    }

    @Test
    fun tokenErrorResponseDtoWithMessageAndPurchaser() {
        val dto =
            json.decodeFromString(
                TokenErrorResponseDto.serializer(),
                """{"message":"err","message_to_purchaser":"Card failed"}""",
            )
        assertEquals("error", dto.objectType)
        assertEquals("", dto.type)
        assertEquals("err", dto.message)
        assertEquals("Card failed", dto.messageToPurchaser)
    }

    @Test
    fun cardDataDtoRoundTrip() {
        val original = CardDataDto("123", "12", "26", "John", "4242424242424242")
        val serialized = json.encodeToString(CardDataDto.serializer(), original)
        val deserialized = json.decodeFromString(CardDataDto.serializer(), serialized)
        assertEquals(original, deserialized)
    }

    @Test
    fun cardPayloadDtoRoundTrip() {
        val original = CardPayloadDto(card = CardDataDto("456", "01", "28", "Jane", "5555555555554444"))
        val serialized = json.encodeToString(CardPayloadDto.serializer(), original)
        val deserialized = json.decodeFromString(CardPayloadDto.serializer(), serialized)
        assertEquals(original, deserialized)
    }

    @Test
    fun tokenRequestDtoRoundTrip() {
        val original = TokenRequestDto(data = "enc_data", key = "enc_key")
        val serialized = json.encodeToString(TokenRequestDto.serializer(), original)
        val deserialized = json.decodeFromString(TokenRequestDto.serializer(), serialized)
        assertEquals(original, deserialized)
    }
}
