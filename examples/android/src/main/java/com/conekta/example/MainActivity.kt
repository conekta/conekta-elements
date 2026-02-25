package com.conekta.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.conekta.compose.checkout.ConektaCheckout
import io.conekta.compose.tokenizer.ConektaTokenizer
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError
import io.conekta.elements.checkout.models.CheckoutOrderResult
import io.conekta.elements.checkout.models.CurrencyCodes
import io.conekta.elements.network.HEADER_ACCEPT_CONEKTA_VERSION
import io.conekta.elements.tokenizer.models.TokenResult
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

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

private const val JWT_TOKEN = "jwt_mock_123"
private const val TOKENIZER_BASE_URL = "https://api.stg.conekta.io"
private const val CHECKOUT_BASE_URL = "https://services.stg.conekta.io"
private const val TAG = "ConektaExample"
private const val ORDERS_URL = "https://api.stg.conekta.io/orders"
private const val TOKENIZER_RSA_PUBLIC_KEY =
    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2YrQXIfVCBU3MfG5gKxL" +
        "Mh8o9kDW2gtb2yu2V6P0GX3yNazTF99Y1lcwI1pKpRBBheFVJ5U3mNCqvFRlIlLj" +
        "flk/pgm1dwzjcDyaks3iYtbJnne8Ahaqam1Lm8RcM/AAiRv28/uHatw4PHQYPCq+" +
        "ZAbSd+uYmozjGp9ISd0XLQSRO95LGrmMD4hBf50B2S+NPuQGO5xdXy/4Fpq6xmmO" +
        "a0kTn6RETCZ4/tWZAsC8g7vjolETyJqyDCwnrtZdE4c2yhfEBz6PAGvdLKrobIOo" +
        "brX2LynMj6oI8i0N3fRl97ScRask6bzWfnpje8iRq3mhvwHmUcP30BMFprXtK3zM" +
        "GwIDAQAB"

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
            ExampleTab.CHECKOUT -> CheckoutExample()
        }
    }
}

@Composable
private fun TokenizerExample() {
    val publicKey = requireConektaPublicKey()
    val context = LocalContext.current
    val appContext = context.applicationContext
    ConektaTokenizer(
        config =
            TokenizerConfig(
                baseUrl = TOKENIZER_BASE_URL,
                publicKey = publicKey,
                merchantName = "My Store",
                rsaPublicKey = TOKENIZER_RSA_PUBLIC_KEY,
            ),
        onSuccess = { result: TokenResult ->
            Log.d(TAG, "Tokenizer success: token=${result.token}, lastFour=${result.lastFour}")
            Handler(Looper.getMainLooper()).post {
                Toast
                    .makeText(
                        appContext,
                        "Tokenizer success: token=${result.token}",
                        Toast.LENGTH_LONG,
                    ).show()
            }
        },
        onError = { error: TokenizerError ->
            val logMessage = when (error) {
                is TokenizerError.ValidationError -> error.message
                is TokenizerError.NetworkError -> error.message
                is TokenizerError.ApiError -> "${error.code}: ${error.message}"
            }
            val uiMessage = when (error) {
                is TokenizerError.ValidationError -> error.message
                is TokenizerError.NetworkError -> error.message
                is TokenizerError.ApiError -> error.message
            }
            Log.e(TAG, "Tokenizer error: $logMessage")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(appContext, uiMessage, Toast.LENGTH_LONG).show()
            }
        },
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun CheckoutExample() {
    val publicKey = requireConektaPublicKey()
    var checkoutRequestId by remember { mutableStateOf<String?>(null) }
    var fetchError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            checkoutRequestId = fetchCheckoutRequestId(publicKey)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch checkout request ID", e)
            fetchError = e.message ?: "Unknown error"
        }
    }

    when {
        fetchError != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error creating order: $fetchError")
            }
        }
        checkoutRequestId == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Creating order...")
            }
        }
        else -> {
            ConektaCheckout(
                config = CheckoutConfig(
                    checkoutRequestId = checkoutRequestId!!,
                    publicKey = publicKey,
                    jwtToken = JWT_TOKEN,
                    merchantName = "My Store",
                    baseUrl = CHECKOUT_BASE_URL,
                    tokenizerBaseUrl = TOKENIZER_BASE_URL,
                    tokenizerRsaPublicKey = TOKENIZER_RSA_PUBLIC_KEY,
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
                onOrderCreated = { result: CheckoutOrderResult ->
                    Log.d(TAG, "Order created: orderId=${result.orderId}")
                },
            )
        }
    }
}

@Suppress("LongMethod")
private suspend fun fetchCheckoutRequestId(publicKey: String): String =
    withContext(Dispatchers.IO) {
        val connection = (URL(ORDERS_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Authorization", "Bearer $publicKey")
            setRequestProperty("Accept-Language", Locale.getDefault().language)
            setRequestProperty("Accept", HEADER_ACCEPT_CONEKTA_VERSION)
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
        }

        val quantity = (1..10).random()
        val unitPrice = (2000..5000).random()
        val customerName = listOf(
            "Ana García", "Carlos López", "María Martínez",
            "Juan Rodríguez", "Laura Sánchez", "Pedro Ramírez",
        ).random()
        val emailSuffix = (1..8).map { "abcdefghijklmnopqrstuvwxyz0123456789".random() }.joinToString("")
        val email = "dev_$emailSuffix@conekta.com"
        val phone = "55${(10000000..99999999).random()}"
        val productName = listOf(
            "Box of Cohiba S1s", "Laptop Pro 15", "Wireless Headphones",
            "Coffee Maker", "Running Shoes", "Smart Watch",
        ).random()

        val body = JSONObject().apply {
            put("currency", CurrencyCodes.MXN)
            put(
                "customer_info",
                JSONObject().apply {
                    put("name", customerName)
                    put("email", email)
                    put("phone", phone)
                    put("object", "customer_info")
                },
            )
            put("line_items", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("name", productName)
                    put("unit_price", unitPrice)
                    put("quantity", quantity)
                })
            })
            put(
                "checkout",
                JSONObject().apply {
                    put(
                        "allowed_payment_methods",
                        org.json.JSONArray().apply {
                            put("cash")
                            put("card")
                            put("bank_transfer")
                            put("bnpl")
                            put("pay_by_bank")
                        },
                    )
                    put("type", "Integration")
                },
            )
        }

        connection.outputStream.use { it.write(body.toString().toByteArray()) }

        val responseCode = connection.responseCode
        val responseBody = if (responseCode in 200..299) {
            connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            error("Order creation failed ($responseCode): $errorBody")
        }

        val json = JSONObject(responseBody)
        json.getJSONObject("checkout").getString("id")
    }

private fun requireConektaPublicKey(): String =
    BuildConfig.CONEKTA_PUBLIC_KEY.takeIf { it.isNotBlank() }
        ?: error(
            "CONEKTA_PUBLIC_KEY is missing. Set it in examples/android/local.properties " +
                "or export CONEKTA_PUBLIC_KEY before building.",
        )
