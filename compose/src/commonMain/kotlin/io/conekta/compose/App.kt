package io.conekta.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.conekta.elements.Greeting
import io.conekta.elements.compose.generated.resources.Res
import io.conekta.elements.compose.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.dp
import io.conekta.elements.domain.entities.Token
import io.conekta.elements.ui.theme.ConektaTheme
import io.conekta.elements.ui.wrappers.Tokenizer
import io.conekta.elements.ui.wrappers.tokenizerConfig

/**
 * Example App showcasing the Tokenizer wrapper
 */
@Composable
fun App() {
    ConektaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TokenizerExampleScreen()
        }
    }
}

@Composable
fun TokenizerExampleScreen() {
    var tokenResult by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Conekta Elements Tokenizer",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 24.dp)
        )
        
        Text(
            text = "Demo de tokenización de tarjetas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Tokenizer Component
        val config = tokenizerConfig(publicKey = "key_demo_xxxxxxxxx") {
            enableMsi = false
            isRecurrent = false
        }
        
        Tokenizer(
            config = config,
            submitButtonText = "Crear Token",
            onTokenCreated = { token ->
                tokenResult = """
                    Token creado exitosamente!
                    
                    ID: ${token.id}
                    Marca: ${token.card.brand}
                    Últimos 4 dígitos: ${token.card.lastFour}
                    Expiración: ${token.card.expiryMonth}/${token.card.expiryYear}
                    Live Mode: ${token.livemode}
                """.trimIndent()
                showDialog = true
                errorMessage = null
            },
            onError = { error ->
                errorMessage = error
                showDialog = true
                tokenResult = null
            }
            AnimatedVisibility(showContent) {
                val greeting =  remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
        )
        
        // Result/Error Display
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = if (tokenResult != null) "✅ Éxito" else "❌ Error"
                    )
                },
                text = {
                    Text(
                        text = tokenResult ?: errorMessage ?: "Error desconocido"
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tarjetas de Prueba",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "• Visa: 4242 4242 4242 4242\n• Mastercard: 5200 0000 0000 1005\n• AMEX: 3782 822463 10005",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "\nCualquier fecha futura y CVV",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
