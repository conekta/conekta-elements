# Conekta Elements - Guía de Integración

## Tokenizer SDK para Android (Compose)

Esta guía te ayudará a integrar el Tokenizer de Conekta Elements en tu aplicación Android.

---

## 📦 Instalación

### 1. Agregar Maven Local (para pruebas)

En tu archivo `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal() // Para la SDK de Conekta Elements
        google()
        mavenCentral()
    }
}
```

### 2. Agregar la Dependencia

En tu archivo `app/build.gradle.kts`:

```kotlin
dependencies {
    // Conekta Elements SDK
    implementation("io.conekta.elements:compose-android:1.0.10")
    
    // Dependencias requeridas (si no las tienes)
    implementation("androidx.compose.material:material-icons-extended")
}
```

### 3. Configurar compileSdk

Asegúrate de tener `compileSdk = 36` o superior:

```kotlin
android {
    compileSdk = 36
    
    defaultConfig {
        minSdk = 24
        targetSdk = 36
    }
}
```

---

## 🚀 Uso Básico

### Ejemplo Completo

```kotlin
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.conekta.compose.models.TokenizerConfig
import io.conekta.compose.models.TokenizerError
import io.conekta.compose.tokenizer.ConektaTokenizer
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
                ConektaTokenizer(
                    config = TokenizerConfig(
                        publicKey = "key_test_xxxxx", // Tu public key de Conekta
                        merchantName = "Mi Tienda",
                        collectCardholderName = true
                    ),
                    onSuccess = { tokenResult ->
                        // Token generado exitosamente
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Token: ${tokenResult.token}"
                            )
                        }
                        
                        // Enviar token a tu backend
                        sendTokenToBackend(tokenResult.token)
                    },
                    onError = { error ->
                        // Manejar errores
                        val errorMessage = when (error) {
                            is TokenizerError.ValidationError -> 
                                "Error de validación: ${error.message}"
                            is TokenizerError.NetworkError -> 
                                "Error de red: ${error.message}"
                            is TokenizerError.ApiError -> 
                                "Error API: ${error.code} - ${error.message}"
                        }
                        
                        scope.launch {
                            snackbarHostState.showSnackbar(errorMessage)
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
    
    private fun sendTokenToBackend(token: String) {
        // Implementa tu lógica para enviar el token a tu backend
        println("Token to send: $token")
    }
}
```

---

## ⚙️ Configuración

### TokenizerConfig

```kotlin
data class TokenizerConfig(
    val publicKey: String,              // Tu public key de Conekta (REQUERIDO)
    val merchantName: String,           // Nombre de tu negocio (REQUERIDO)
    val collectCardholderName: Boolean = true  // Si se debe recolectar el nombre del tarjetahabiente
)
```

### Callbacks

#### onSuccess

Se llama cuando el token se genera exitosamente:

```kotlin
onSuccess = { tokenResult ->
    // tokenResult.token: String - El token generado
    // tokenResult.cardBrand: String - Marca de la tarjeta (visa, mastercard, amex)
    // tokenResult.lastFour: String - Últimos 4 dígitos
}
```

#### onError

Se llama cuando ocurre un error:

```kotlin
onError = { error ->
    when (error) {
        is TokenizerError.ValidationError -> {
            // Error de validación de campos
            // error.message: String
        }
        is TokenizerError.NetworkError -> {
            // Error de conexión
            // error.message: String
        }
        is TokenizerError.ApiError -> {
            // Error de la API de Conekta
            // error.code: String
            // error.message: String
        }
    }
}
```

---

## 🎨 Personalización

### Tema

El Tokenizer usa el tema de Conekta por defecto, pero respeta el tema de Material3 de tu app para componentes base.

### Modificador

Puedes personalizar el layout usando el parámetro `modifier`:

```kotlin
ConektaTokenizer(
    config = config,
    onSuccess = { /* ... */ },
    onError = { /* ... */ },
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(Color.White)
)
```

---

## 🔒 Seguridad

### Mejores Prácticas

1. **Nunca** almacenes el token en el dispositivo
2. **Siempre** usa HTTPS para enviar el token a tu backend
3. **No** envíes el token directamente a servicios de terceros
4. **Usa** tu public key, nunca tu private key
5. **Valida** el token en tu backend antes de procesar el pago

### Flujo Recomendado

```
Usuario → Tokenizer → Token → Tu Backend → Conekta API → Cargo
```

---

## 📱 Requisitos

- **Android**: API 24+ (Android 7.0+)
- **Kotlin**: 2.2.20+
- **Compose**: 1.10.0+
- **Material3**: 1.9.0+

---

## 🐛 Troubleshooting

### Error: "Unresolved reference: compose"

Asegúrate de tener `mavenLocal()` en tu `settings.gradle.kts`.

### Error: "Unresolved reference: Icons"

Agrega la dependencia de Material Icons:

```kotlin
implementation("androidx.compose.material:material-icons-extended")
```

### Error: "Requires Android Gradle plugin 8.9.1 or higher"

Actualiza tu AGP en `build.gradle.kts`:

```kotlin
plugins {
    id("com.android.application") version "8.9.1" apply false
}
```

---

## 📚 Recursos

- [Documentación de Conekta](https://developers.conekta.com/)
- [API Reference](https://developers.conekta.com/api)
- [Ejemplo Completo](../conekta-demo-simple/)

---

## 🆘 Soporte

Para soporte técnico, contacta a:
- Email: soporte@conekta.com
- Documentación: https://developers.conekta.com/

---

**Versión**: 1.0.10  
**Última actualización**: 2026-01-22

