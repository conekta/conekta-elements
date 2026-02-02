package io.conekta.compose.tokenizer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.components.CardBrandIcon
import io.conekta.compose.components.CardBrandIconsRow
import io.conekta.compose.components.CheckCircleIcon
import io.conekta.compose.components.ConektaButton
import io.conekta.compose.components.ConektaTextField
import io.conekta.compose.components.ConektaLogoImage
import io.conekta.elements.tokenizer.models.CardBrand
import io.conekta.elements.tokenizer.models.TokenResult
import io.conekta.elements.tokenizer.models.TokenizerConfig
import io.conekta.elements.tokenizer.models.TokenizerError
import io.conekta.elements.theme.ConektaColors
import io.conekta.compose.theme.ConektaTheme

/**
 * Conekta Tokenizer - Main public API
 * 
 * A composable that renders a complete card tokenization form
 * following Conekta's design system.
 *
 * @param config Configuration for the tokenizer
 * @param onSuccess Callback when tokenization succeeds
 * @param onError Callback when tokenization fails
 * @param modifier Optional modifier for the tokenizer container
 *
 * Example usage:
 * ```
 * ConektaTokenizer(
 *     config = TokenizerConfig(
 *         publicKey = "key_xxx",
 *         merchantName = "My Store"
 *     ),
 *     onSuccess = { token -> /* handle token */ },
 *     onError = { error -> /* handle error */ }
 * )
 * ```
 */
@Composable
fun ConektaTokenizer(
    config: TokenizerConfig,
    onSuccess: (TokenResult) -> Unit,
    onError: (TokenizerError) -> Unit,
    modifier: Modifier = Modifier
) {
    ConektaTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color.White
        ) {
            TokenizerContent(
                config = config,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
}

@Composable
private fun TokenizerContent(
    config: TokenizerConfig,
    onSuccess: (TokenResult) -> Unit,
    onError: (TokenizerError) -> Unit
) {
    var cardholderName by remember { mutableStateOf(TextFieldValue("")) }
    var cardNumber by remember { mutableStateOf(TextFieldValue("")) }
    var expiryDate by remember { mutableStateOf(TextFieldValue("")) }
    var cvv by remember { mutableStateOf(TextFieldValue("")) }
    var showProtectionSheet by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    
    // Error states for each field
    var cardholderNameError by remember { mutableStateOf(false) }
    var cardNumberError by remember { mutableStateOf(false) }
    var expiryDateError by remember { mutableStateOf(false) }
    var cvvError by remember { mutableStateOf(false) }
    
    // Error messages for each field
    var cardholderNameErrorMsg by remember { mutableStateOf<String?>(null) }
    var cardNumberErrorMsg by remember { mutableStateOf<String?>(null) }
    var expiryDateErrorMsg by remember { mutableStateOf<String?>(null) }
    var cvvErrorMsg by remember { mutableStateOf<String?>(null) }
    
    val detectedBrand = remember(cardNumber.text) {
        CardFormatters.detectCardBrand(cardNumber.text)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        TokenizerHeader(
            merchantName = config.merchantName,
            onInfoClick = { showProtectionSheet = true }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Title
        Text(
            text = "Información de la tarjeta",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = ConektaColors.DarkIndigo,
                lineHeight = 28.sp
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Form
        if (config.collectCardholderName) {
            ConektaTextField(
                value = cardholderName,
                onValueChange = { 
                    cardholderName = it
                    cardholderNameError = false // Clear error on input
                    cardholderNameErrorMsg = null
                },
                label = "Nombre en la tarjeta",
                placeholder = "Nombre como aparece en la tarjeta",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                enabled = !isProcessing,
                isError = cardholderNameError,
                errorMessage = cardholderNameErrorMsg
            )
        }
        
        // Card Number with brand icon
        ConektaTextField(
            value = cardNumber,
            onValueChange = { newValue ->
                cardNumber = CardFormatters.formatCardNumber(newValue)
                cardNumberError = false // Clear error on input
                cardNumberErrorMsg = null
            },
            label = "Número de tarjeta",
            placeholder = "0000 0000 0000 0000",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
            enabled = !isProcessing,
            isError = cardNumberError,
            errorMessage = cardNumberErrorMsg,
            trailingContent = {
                // Show card brand icons
                CardBrandIconsRow(
                    detectedBrand = detectedBrand
                )
            }
        )
        
        // Expiry and CVV Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ConektaTextField(
                value = expiryDate,
                onValueChange = { newValue ->
                    expiryDate = CardFormatters.formatExpiryDate(newValue)
                    expiryDateError = false // Clear error on input
                    expiryDateErrorMsg = null
                },
                label = "Expiración",
                placeholder = "MM/YY",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                modifier = Modifier.weight(1f),
                enabled = !isProcessing,
                isError = expiryDateError,
                errorMessage = expiryDateErrorMsg
            )
            
            ConektaTextField(
                value = cvv,
                onValueChange = { newValue ->
                    cvv = CardFormatters.formatCvv(newValue, detectedBrand)
                    cvvError = false // Clear error on input
                    cvvErrorMsg = null
                },
                label = "Código de seguridad",
                placeholder = "CVV",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                modifier = Modifier.weight(1f),
                enabled = !isProcessing,
                isError = cvvError,
                errorMessage = cvvErrorMsg
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Submit Button
        ConektaButton(
            text = if (isProcessing) "Procesando..." else "Continuar",
            onClick = {
                // Clear all errors first
                cardholderNameError = false
                cardholderNameErrorMsg = null
                cardNumberError = false
                cardNumberErrorMsg = null
                expiryDateError = false
                expiryDateErrorMsg = null
                cvvError = false
                cvvErrorMsg = null
                
                // Validate all fields
                val cardDigits = cardNumber.text.filter { it.isDigit() }
                var hasError = false
                
                // Validate each field and mark errors
                if (config.collectCardholderName && cardholderName.text.isBlank()) {
                    cardholderNameError = true
                    cardholderNameErrorMsg = "Este dato es necesario"
                    hasError = true
                }
                
                if (cardNumber.text.isBlank() || !CardFormatters.isValidCardNumber(cardDigits)) {
                    cardNumberError = true
                    cardNumberErrorMsg = "Este dato es necesario"
                    hasError = true
                }
                
                if (expiryDate.text.isBlank() || !CardFormatters.isValidExpiryDate(expiryDate.text)) {
                    expiryDateError = true
                    expiryDateErrorMsg = "Este dato es necesario"
                    hasError = true
                }
                
                if (cvv.text.isBlank() || !CardFormatters.isValidCvv(cvv.text, detectedBrand)) {
                    cvvError = true
                    cvvErrorMsg = "Este dato es necesario"
                    hasError = true
                }
                
                // If there are no errors, proceed with tokenization
                if (!hasError) {
                    isProcessing = true
                    // TODO: Implement actual tokenization API call
                    // For now, return a mock token
                    onSuccess(
                        TokenResult(
                            token = "tok_test_${System.currentTimeMillis()}",
                            cardBrand = detectedBrand.name,
                            lastFour = cardDigits.takeLast(4)
                        )
                    )
                    isProcessing = false
                }
                // If there are errors, they are already displayed below each input
            },
            enabled = !isProcessing
        )
    }
    
    // Payment Protection Sheet
    if (showProtectionSheet) {
        PaymentProtectionSheet(
            merchantName = config.merchantName,
            onDismiss = { showProtectionSheet = false }
        )
    }
}

@Composable
private fun TokenizerHeader(
    merchantName: String,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "PAGA SEGURO CON",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = ConektaColors.Neutral7,
                    lineHeight = 16.sp,
                    letterSpacing = 0.5.sp
                )
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Conekta Logo
            ConektaLogoImage(
                modifier = Modifier.height(20.dp)
            )
        }
        
        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Información de seguridad",
                tint = ConektaColors.Neutral7
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentProtectionSheet(
    merchantName: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { 
            BottomSheetDefaults.DragHandle(
                color = ConektaColors.Neutral5
            ) 
        },
        containerColor = ConektaColors.Surface,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(bottom = 32.dp)
        ) {
            // Close button (X) at top right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Text(
                        text = "✕",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = ConektaColors.DarkIndigo
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title with check icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Green check icon from Figma
                CheckCircleIcon(
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Tu pago está protegido",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ConektaColors.DarkIndigo,
                        lineHeight = 20.sp
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description text
            Text(
                text = "Conekta es el portal que usa $merchantName para procesar sus pagos en línea de manera segura y confiable.",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ConektaColors.Neutral8,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

