# Conekta Elements - Compose Module

SDK de UI para tokenización de tarjetas y checkout en Android usando Jetpack Compose.

## Instalación

```kotlin
dependencies {
    implementation("io.conekta:conekta-elements-compose:0.2.0-beta.2")
}
```

### Configuración de Coil (requerida)

La librería usa Coil para cargar iconos de tarjetas (SVG) desde CDN. Crea una clase `Application` e inicializa Coil:

```kotlin
import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import io.conekta.compose.ConektaImageLoader

class MyApp : Application(), SingletonImageLoader.Factory {
    override fun newImageLoader(context: android.content.Context): ImageLoader {
        return ConektaImageLoader.newImageLoader(context)
    }
}
```

Registra en tu `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApp"
    ... >
```

## Uso Básico

### Tokenizer

```kotlin
import io.conekta.compose.tokenizer.ConektaTokenizer
import io.conekta.elements.tokenizer.models.TokenizerConfig

@Composable
fun MyPaymentScreen() {
    ConektaTokenizer(
        config = TokenizerConfig(
            publicKey = "key_xxxxx",
            merchantName = "Mi Tienda",
            collectCardholderName = true
        ),
        onSuccess = { tokenResult ->
            // Usar el token para procesar el pago
            println("Token: ${tokenResult.token}")
            println("Últimos 4 dígitos: ${tokenResult.lastFour}")
            println("Marca: ${tokenResult.cardBrand}")
        },
        onError = { error ->
            // Manejar el error
            when (error) {
                is TokenizerError.ValidationError -> {
                    println("Error de validación: ${error.message}")
                }
                is TokenizerError.NetworkError -> {
                    println("Error de red: ${error.message}")
                }
                is TokenizerError.ApiError -> {
                    println("Error API: ${error.code} - ${error.message}")
                }
            }
        }
    )
}
```

### Checkout dinámico (métodos de pago)

```kotlin
import io.conekta.compose.checkout.ConektaCheckout
import io.conekta.elements.checkout.models.CheckoutConfig
import io.conekta.elements.checkout.models.CheckoutError

@Composable
fun MyCheckoutScreen() {
    ConektaCheckout(
        config = CheckoutConfig(
            checkoutRequestId = "dc5baf10-0f2b-4378-9f74-afa6bb418198",
            publicKey = "key_xxxxx",
            jwtToken = "jwt_xxxxx",
        ),
        onPaymentMethodSelected = { method ->
            println("Selected method: $method")
        },
        onError = { error: CheckoutError ->
            println("Checkout error: $error")
        },
    )
}
```

## Características

### Diseño Basado en Figma
- Sigue el sistema de diseño de Conekta
- Colores, tipografía y espaciado consistentes
- Componentes reutilizables

### Validación Automática
- Validación de número de tarjeta (algoritmo de Luhn)
- Validación de fecha de expiración
- Validación de CVV según marca de tarjeta
- Detección automática de marca (Visa, Mastercard, Amex)

### Formato Automático
- Número de tarjeta: espacios cada 4 dígitos
- Fecha de expiración: formato MM/YY
- CVV: 3-4 dígitos según marca
- Preserva posición del cursor durante formato

### Modal de Información
- "Tu pago está protegido"
- Arrastrable y con cierre al tocar fuera
- Información sobre seguridad de Conekta

### Localización
- Soporte para Español e Inglés

## Configuración

### TokenizerConfig

```kotlin
data class TokenizerConfig(
    val publicKey: String,              // Tu public key de Conekta
    val merchantName: String = "Demo Store",  // Nombre de tu tienda
    val collectCardholderName: Boolean = true // Recolectar nombre del tarjetahabiente
)
```

### TokenResult

```kotlin
data class TokenResult(
    val token: String,      // Token generado
    val cardBrand: String,  // VISA, MASTERCARD, AMEX
    val lastFour: String    // Últimos 4 dígitos
)
```

### TokenizerError

```kotlin
sealed class TokenizerError {
    data class ValidationError(val message: String) : TokenizerError()
    data class NetworkError(val message: String) : TokenizerError()
    data class ApiError(val code: String, val message: String) : TokenizerError()
}
```

## Arquitectura

```
compose/
├── theme/
│   ├── ConektaColors.kt    # Sistema de colores
│   └── ConektaTheme.kt     # Tema Material3
├── components/
│   ├── ConektaTextField.kt  # Campo de texto personalizado
│   └── ConektaButton.kt    # Botón personalizado
├── tokenizer/
│   ├── ConektaTokenizer.kt # Componente principal (API pública)
│   └── CardFormatters.kt   # Utilidades de formato y validación
├── checkout/
│   └── ConektaCheckout.kt  # Componente de checkout dinámico
├── localization/
│   └── Strings (ES/EN)     # Cadenas localizadas
└── utils/
    └── Utilidades internas
```

## Próximos Pasos

- [ ] Agregar soporte para MSI (Meses Sin Intereses)
- [ ] Agregar animaciones de transición
- [ ] Soporte para temas oscuros

## Notas de Desarrollo

### Módulo `shared/`
El módulo `shared` contiene solo lógica de negocio pura (sin UI):
- Validaciones
- Modelos de datos
- Cliente HTTP
- Utilidades

### Módulo `compose/`
El módulo `compose` contiene todos los componentes de UI:
- Composables
- Tema y colores
- Recursos (drawables, strings)
- API pública del SDK
