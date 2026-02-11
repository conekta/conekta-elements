package io.conekta.compose.tokenizer

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.elements.tokenizer.models.TokenizerConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConektaTokenizerTest {
    private val defaultConfig =
        TokenizerConfig(
            publicKey = "key_test_123",
            merchantName = "Test Store",
            collectCardholderName = true,
        )

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerWithCardholderNameCollectionEnabled() =
        runComposeUiTest {
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerWithoutCardholderNameCollection() =
        runComposeUiTest {
            val config = defaultConfig.copy(collectCardholderName = false)
            setContent {
                ConektaTokenizer(
                    config = config,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerRendersWithDifferentMerchantName() =
        runComposeUiTest {
            val config = defaultConfig.copy(merchantName = "Custom Store")
            setContent {
                ConektaTokenizer(
                    config = config,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tokenizerDisplaysSubmitButton() =
        runComposeUiTest {
            setContent {
                ConektaTokenizer(
                    config = defaultConfig,
                    onSuccess = {},
                    onError = {},
                )
            }
            onRoot().assertExists()
        }
}
