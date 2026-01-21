# Conekta Elements - Kotlin Compose Multiplatform

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)
![Compose](https://img.shields.io/badge/Compose-1.6.0-green.svg)
![Platform](https://img.shields.io/badge/platform-Android%20%7C%20iOS-lightgrey.svg)
![Status](https://img.shields.io/badge/status-Ready-success.svg)

Biblioteca de componentes UI para integrar Conekta en aplicaciones Android e iOS usando Kotlin Compose Multiplatform.

## 🚀 **¿Quieres ejecutarlo AHORA?** → Lee [`START_HERE.md`](START_HERE.md)

## 📖 **Guías Rápidas**
- 🎯 [`EJECUTAR_AHORA.md`](EJECUTAR_AHORA.md) - Cómo ejecutar en Android/iOS
- ⚡ [`QUICK_START.md`](QUICK_START.md) - Inicio rápido en 5 minutos
- 📊 [`RESUMEN_FINAL.md`](RESUMEN_FINAL.md) - Resumen ejecutivo completo

## 🎯 Características

- **🎨 Componentes Atómicos**: Arquitectura basada en Atomic Design para máxima reutilización
- **🏗️ Arquitectura Limpia**: Separación de concerns en capas (Domain, Data, Presentation, UI)
- **📱 Multiplatform**: Funciona en Android e iOS con código compartido
- **✅ Validación en Tiempo Real**: Feedback instantáneo con validadores integrados
- **🎭 Personalizable**: Temas y estilos personalizables
- **🔒 Seguro**: Datos sensibles nunca pasan por tus servidores
- **📦 Modular**: 3 wrappers independientes (Tokenizer, Component, Express Checkout)

## 📦 Wrappers Disponibles

### 1. Tokenizer ✅ (Implementado)
Tokeniza tarjetas de crédito/débito de forma segura.

```kotlin
Tokenizer(
    config = tokenizerConfig(publicKey = "key_xxx"),
    onTokenCreated = { token -> /* ... */ },
    onError = { error -> /* ... */ }
)
```

[Ver documentación completa →](TOKENIZER_README.md)

### 2. Component 🚧 (Próximamente)
Componente completo de pago con múltiples métodos de pago.

### 3. Express Checkout 🚧 (Próximamente)
Checkout express con Apple Pay, Google Pay, etc.

## 🚀 Instalación

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.conekta:elements:1.0.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'io.conekta:elements:1.0.0'
}
```

## 📖 Inicio Rápido

### 1. Inicializar el SDK (Opcional)

```kotlin
import io.conekta.elements.ConektaElements

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ConektaElements.initialize(publicKey = "key_xxxxxxxxxxxxx")
    }
}
```

### 2. Usar el Tokenizer

```kotlin
import io.conekta.elements.ui.wrappers.Tokenizer
import io.conekta.elements.ui.wrappers.tokenizerConfig
import io.conekta.elements.ui.theme.ConektaTheme

@Composable
fun CheckoutScreen() {
    ConektaTheme {
        val config = tokenizerConfig(publicKey = "key_xxxxxxxxxxxxx") {
            enableMsi = true
            customerName = "Juan Pérez"
        }
        
        Tokenizer(
            config = config,
            submitButtonText = "Pagar $1,200 MXN",
            onTokenCreated = { token ->
                // Enviar token a tu backend
                processPayment(token.id)
            },
            onError = { error ->
                showError(error)
            }
        )
    }
}
```

## 🏗️ Arquitectura

El proyecto sigue Clean Architecture con 4 capas principales:

```
┌─────────────────────────────────────┐
│       UI Layer (Composables)        │  ← Wrappers y componentes
├─────────────────────────────────────┤
│   Presentation Layer (ViewModels)   │  ← Estado y lógica de UI
├─────────────────────────────────────┤
│   Domain Layer (Business Logic)     │  ← Casos de uso, validadores
├─────────────────────────────────────┤
│     Data Layer (Repositories)       │  ← API, fuentes de datos
└─────────────────────────────────────┘
```

[Ver documentación de arquitectura completa →](ARCHITECTURE.md)

## 🧩 Componentes Atómicos

Los componentes están organizados siguiendo Atomic Design:

### Atoms (Básicos)
- `ConektaTextField` - Campo de texto base
- `ConektaButton` - Botón base

### Molecules (Especializados)
- `CardNumberField` - Campo de número de tarjeta
- `CardholderNameField` - Campo de nombre del titular
- `ExpiryDateField` - Campo de fecha de expiración
- `CvvField` - Campo CVV/CVC

### Organisms (Complejos)
- `FormTokenizer` - Formulario completo de tokenización

### Wrappers (Alto Nivel)
- `Tokenizer` - Wrapper del tokenizer
- `Component` - Wrapper del componente (próximamente)
- `ExpressCheckout` - Wrapper express checkout (próximamente)

## 🎨 Personalización

### Tema Personalizado

```kotlin
import androidx.compose.material3.lightColorScheme
import io.conekta.elements.ui.theme.ConektaTheme

val customColors = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    // ... más colores
)

ConektaTheme(colorScheme = customColors) {
    Tokenizer(/* ... */)
}
```

### Mensajes de Error Personalizados

```kotlin
val spanishErrors = ValidationErrorMessages(
    required = "Este campo es obligatorio",
    invalidCard = "Número de tarjeta inválido",
    expiredCard = "Tarjeta expirada"
)

Tokenizer(
    config = config,
    errorMessages = spanishErrors,
    onTokenCreated = { /* ... */ },
    onError = { /* ... */ }
)
```

## 🔐 Seguridad

- ✅ Datos sensibles nunca tocan tus servidores
- ✅ Comunicación HTTPS con API de Conekta
- ✅ CVV enmascarado en UI
- ✅ Validación en cliente y servidor
- ✅ Tokens de un solo uso por defecto

## 📱 Plataformas Soportadas

- ✅ Android (API 24+)
- ✅ iOS (iOS 13+)
- 🚧 Desktop (próximamente)
- 🚧 Web (próximamente)

## 🧪 Testing

```kotlin
@Test
fun `validates card number correctly`() {
    val validator = CardNumberValidator(ValidationErrorMessages())
    
    // Valid Visa card
    val result = validator.validate("4242424242424242")
    assertTrue(result.isValid)
}
```

## 📚 Documentación

- [Tokenizer README](TOKENIZER_README.md)
- [Arquitectura](ARCHITECTURE.md)
- [API Reference](docs/api/)
- [Ejemplos](examples/)

## 🛠️ Desarrollo

### Requisitos

- Kotlin 1.9.22+
- Gradle 8.0+
- Android Studio Hedgehog+
- Xcode 15+ (para iOS)

### Compilar

```bash
./gradlew build
```

### Ejecutar Tests

```bash
./gradlew test
```

### Generar Documentación

```bash
./gradlew dokkaHtml
```

## 🗺️ Roadmap

### v1.0.0 ✅
- [x] Tokenizer wrapper
- [x] Validadores de tarjeta
- [x] Componentes atómicos base
- [x] Soporte Android/iOS
- [x] Arquitectura limpia

### v1.1.0 🚧
- [ ] Component wrapper (pago completo)
- [ ] Device fingerprinting
- [ ] 3D Secure
- [ ] Meses sin intereses (MSI)

### v1.2.0 🔮
- [ ] Express Checkout wrapper
- [ ] Apple Pay integration
- [ ] Google Pay integration
- [ ] Soporte para guardar tarjetas

### v2.0.0 🔮
- [ ] Soporte Web (Compose for Web)
- [ ] Desktop support
- [ ] Analytics integrado
- [ ] A/B testing

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Por favor:

1. Fork el proyecto
2. Crea una rama feature (`git checkout -b feature/amazing-feature`)
3. Commit tus cambios (`git commit -m 'Add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request

## 📄 Licencia

MIT License - ver [LICENSE](LICENSE) para más detalles.

## 💬 Soporte

- 📧 Email: soporte@conekta.com
- 📚 Documentación: https://developers.conekta.com
- 💬 Chat: En tu dashboard de Conekta
- 🐛 Issues: GitHub Issues

## 🙏 Agradecimientos

- React implementation inspiration
- Compose Multiplatform team
- Conekta development team

---

Hecho con ❤️ por Conekta
