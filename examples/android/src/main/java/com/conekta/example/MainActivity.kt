package com.conekta.example

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import io.conekta.compose.checkout.ConektaCheckout
import io.conekta.compose.tokenizer.ConektaTokenizer
import io.conekta.elements.checkout.api.CheckoutApiService
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutAmountLine
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutLineItem
import io.conekta.elements.checkout.models.CheckoutPaymentMethods
import io.conekta.elements.checkout.models.CheckoutResult
import io.conekta.elements.tokenizer.models.TokenResult
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ExampleTabs()
                }
            }
        }
    }
}

private const val CHECKOUT_REQUEST_ID = "dc5baf10-0f2b-4378-9f74-afa6bb418198"
private const val CONEKTA_PUBLIC_KEY = "key_mock_123"
private const val JWT_TOKEN = "jwt_mock_123"
private const val TAG = "ConektaExample"

private enum class ExampleTab(
    val label: String,
) {
    TOKENIZER("Tokenizer"),
    CHECKOUT("Checkout"),
}

@Composable
private fun ExampleTabs() {
    var selectedTab by rememberSaveable { mutableStateOf(ExampleTab.TOKENIZER.ordinal) }
    val tabs = ExampleTab.entries

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(tab.label) },
                )
            }
        }

        when (tabs[selectedTab]) {
            ExampleTab.TOKENIZER -> TokenizerExample()
            ExampleTab.CHECKOUT -> CheckoutMockExample()
        }
    }
}

@Composable
private fun TokenizerExample() {
    ConektaTokenizer(
        config =
            TokenizerConfig(
                publicKey = CONEKTA_PUBLIC_KEY,
                merchantName = "My Store",
            ),
        onSuccess = { result: TokenResult ->
            Log.d(TAG, "Tokenizer success: token=${result.token}, lastFour=${result.lastFour}")
        },
        onError = { error: TokenizerError ->
            val message = when (error) {
                is TokenizerError.ValidationError -> error.message
                is TokenizerError.NetworkError -> error.message
                is TokenizerError.ApiError -> "${error.code}: ${error.message}"
            }
            Log.e(TAG, "Tokenizer error: $message")
        },
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun CheckoutMockExample() {
    ConektaCheckout(
        config = CheckoutConfig(
            checkoutRequestId = CHECKOUT_REQUEST_ID,
            publicKey = CONEKTA_PUBLIC_KEY,
            jwtToken = JWT_TOKEN,
            merchantName = "My Store",
        ),
        onPaymentMethodSelected = { method ->
            Log.d(TAG, "Payment method selected: $method")
        },
        onError = { error ->
            val message = when (error) {
                is CheckoutError.ValidationError -> error.message
                is CheckoutError.NetworkError -> error.message
                is CheckoutError.ApiError -> "${error.code}: ${error.message}"
            }
            Log.e(TAG, "Checkout error: $message")
        },
        checkoutApiServiceFactory = { config ->
            object : CheckoutApiService(config) {
                override suspend fun fetchCheckout(): Result<CheckoutResult> =
                    Result.success(
                        CheckoutResult(
                            orderId = "ord_2zb4KeLHjraBbRJgs",
                            checkoutId = "dc5baf10-0f2b-4378-9f74-afa6bb418198",
                            amount = 12000,
                            currency = "MXN",
                            allowedPaymentMethods =
                                listOf(
                                    CheckoutPaymentMethods.CARD,
                                    "bnpl",
                                    CheckoutPaymentMethods.CASH,
                                    "pay_by_bank",
                                    CheckoutPaymentMethods.BANK_TRANSFER,
                                    "apple",
                                ),
                            lineItems =
                                listOf(
                                    CheckoutLineItem(
                                        name = "Aretes Tres Círculos Numerales",
                                        quantity = 1,
                                        unitPrice = 10000,
                                    ),
                                ),
                            taxLines =
                                listOf(
                                    CheckoutAmountLine(
                                        description = "Test",
                                        amount = 2000,
                                    ),
                                ),
                        ),
                    )
            }
        },
    )
}
