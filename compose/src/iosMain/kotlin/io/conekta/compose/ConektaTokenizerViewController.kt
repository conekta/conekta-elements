package io.conekta.compose

import androidx.compose.ui.window.ComposeUIViewController
import io.conekta.compose.tokenizer.ConektaTokenizer
import io.conekta.elements.tokenizer.models.TokenResult
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
import platform.UIKit.UIViewController

/**
 * Creates a UIViewController that hosts the Conekta Tokenizer Compose UI
 *
 * This function is exposed to iOS/Swift code and can be used to embed
 * the Conekta tokenizer into a SwiftUI view or UIKit view controller.
 *
 * @param config Configuration for the tokenizer
 * @param onSuccess Callback when tokenization succeeds
 * @param onError Callback when tokenization fails
 * @return A UIViewController containing the Compose UI
 */
@Suppress("ktlint:standard:function-naming")
fun ConektaTokenizerViewController(
    config: TokenizerConfig,
    onSuccess: (TokenResult) -> Unit,
    onError: (TokenizerError) -> Unit,
): UIViewController =
    ComposeUIViewController {
        ConektaTokenizer(
            config = config,
            onSuccess = onSuccess,
            onError = onError,
        )
    }
