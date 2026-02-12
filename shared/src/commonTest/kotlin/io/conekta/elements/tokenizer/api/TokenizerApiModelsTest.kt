package io.conekta.elements.tokenizer.api

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

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
        assert(serialized.contains("\"exp_month\":\"12\"")) { "Expected snake_case exp_month, got: $serialized" }
        assert(serialized.contains("\"exp_year\":\"26\"")) { "Expected snake_case exp_year, got: $serialized" }
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
        assert(serialized.contains("\"card\":{")) { "Expected nested card object, got: $serialized" }
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
}
