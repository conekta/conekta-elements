package com.conekta.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.conekta.compose.tokenizer.ConektaTokenizer
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TokenizerExample()
                }
            }
        }
    }
}

// ⚠️ Replace with your Conekta public key (see README)
private const val CONEKTA_PUBLIC_KEY = "YOUR_PUBLIC_KEY_HERE"

@Composable
fun TokenizerExample() {
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(dialogTitle) },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
        )
    }

    ConektaTokenizer(
        config = TokenizerConfig(
            publicKey = CONEKTA_PUBLIC_KEY,
            merchantName = "My Store",
        ),
        onSuccess = { tokenResult ->
            dialogTitle = "Token Created"
            dialogMessage = "Token: ${tokenResult.token}\nLast 4: ${tokenResult.lastFour}"
            showDialog = true
        },
        onError = { error ->
            dialogTitle = "Error"
            dialogMessage = when (error) {
                is TokenizerError.ValidationError -> error.message
                is TokenizerError.NetworkError -> error.message
                is TokenizerError.ApiError -> "${error.code}: ${error.message}"
            }
            showDialog = true
        },
    )
}
