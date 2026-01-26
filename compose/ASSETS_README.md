# Conekta Elements - Assets

## 📦 Recursos Incluidos

Los siguientes assets han sido extraídos directamente del diseño de Figma y están incluidos en la SDK:

### 🎨 Iconos de Tarjetas

Ubicación: `compose/src/androidMain/res/drawable/`

1. **ic_visa_card.xml** - Logo de Visa
   - Tamaño: 24x8dp
   - Color: #1434CB (Azul Visa)
   - Formato: Vector Drawable

2. **ic_mastercard_card.xml** - Logo de Mastercard
   - Tamaño: 24x15dp
   - Colores: #FF5F00, #EB001B, #F79E1B
   - Formato: Vector Drawable

3. **ic_amex_card.xml** - Logo de American Express
   - Tamaño: 24x21dp
   - Color: #016FD0 (Azul Amex)
   - Formato: Vector Drawable

### 🏢 Logo de Conekta

4. **ic_conekta_logo.xml** - Logo completo de Conekta
   - Tamaño: 93x19dp
   - Color: #081133 (Dark Indigo)
   - Formato: Vector Drawable
   - Incluye el logotipo completo con texto

---

## 🔧 Uso en la SDK

### Implementación Android

Los iconos están disponibles a través de las funciones Composable específicas de Android:

```kotlin
// Logo de Conekta
ConektaLogoImage(
    modifier = Modifier.height(20.dp)
)

// Icono de marca de tarjeta individual
CardBrandIcon(
    brand = CardBrand.VISA,
    modifier = Modifier.size(24.dp)
)

// Fila de iconos de tarjetas
CardBrandIconsRow(
    detectedBrand = detectedBrand,
    modifier = Modifier
)
```

### Acceso Directo a Recursos

Los recursos también pueden ser accedidos directamente:

```kotlin
val context = LocalContext.current
val logoResId = context.resources.getIdentifier(
    "ic_conekta_logo",
    "drawable",
    context.packageName
)
```

---

## 📋 Extracción desde Figma

### Proceso de Extracción

1. **Fuente**: Figma MCP (Managed Component Platform)
2. **Archivo**: Component Design System
3. **Node ID**: 8505-61154 (Métodos de pago - tarjeta)

### URLs de Assets Originales

Los assets fueron descargados desde las siguientes URLs de Figma:

- **Visa**: `https://www.figma.com/api/mcp/asset/c5eba85d-160f-435e-b9dd-9c70db88b19d`
- **Mastercard**: `https://www.figma.com/api/mcp/asset/185d422c-86d5-42b1-adf6-70185665075d`
- **Amex**: `https://www.figma.com/api/mcp/asset/da943516-6d54-4267-a528-04e89285e6d0`
- **Conekta Logo**: `https://www.figma.com/api/mcp/asset/acd66a97-d12e-48b6-a000-61c371825ab3`

### Conversión a Android

Los SVGs de Figma fueron convertidos a Vector Drawables de Android:

1. **Preservación de paths**: Todos los paths SVG fueron convertidos a `<path>` de Android
2. **Colores**: Los colores `var(--fill-0, ...)` fueron reemplazados por colores hex directos
3. **ViewBox**: Convertido a `viewportWidth` y `viewportHeight`
4. **Dimensiones**: Definidas en `dp` para Android

---

## 🎯 Características

### ✅ Ventajas

- **Vectoriales**: Escalables sin pérdida de calidad
- **Ligeros**: Tamaño mínimo en el APK
- **Consistentes**: Extraídos directamente del diseño oficial
- **Optimizados**: Formato nativo de Android
- **Sin dependencias**: No requieren librerías externas

### 📐 Especificaciones Técnicas

| Asset | Ancho | Alto | Colores | Tamaño (bytes) |
|-------|-------|------|---------|----------------|
| Visa | 24dp | 8dp | 1 | ~1.4KB |
| Mastercard | 24dp | 15dp | 3 | ~1.4KB |
| Amex | 24dp | 21dp | 2 | ~1.5KB |
| Conekta Logo | 93dp | 19dp | 1 | ~3.8KB |

---

## 🔄 Actualización de Assets

### Para actualizar los assets desde Figma:

1. **Conectar al MCP de Figma**
2. **Obtener design context** del node correspondiente
3. **Descargar SVGs** desde las URLs proporcionadas
4. **Convertir a Vector Drawable** usando el formato Android
5. **Reemplazar archivos** en `src/androidMain/res/drawable/`
6. **Republicar SDK** en Maven

### Comando de ejemplo:

```bash
# Descargar asset
curl -k -o /tmp/visa.svg "https://www.figma.com/api/mcp/asset/[ID]"

# Convertir manualmente a Vector Drawable
# (Usar herramientas como svg2android o conversión manual)

# Republicar SDK
cd conekta-elements
./gradlew :compose:publishToMavenLocal
```

---

## 📱 Soporte Multiplataforma

### Android
✅ **Implementado** - Vector Drawables nativos

### iOS
⏳ **Pendiente** - Se requiere implementación con SF Symbols o assets nativos

### Web
⏳ **Pendiente** - Se puede usar SVG directamente

---

## 🐛 Troubleshooting

### Los iconos no se muestran

1. **Verificar que la SDK esté actualizada**:
   ```bash
   ./gradlew :compose:publishToMavenLocal
   ```

2. **Limpiar y recompilar el proyecto**:
   ```bash
   ./gradlew clean assembleDebug
   ```

3. **Verificar que los recursos existan**:
   ```bash
   ls compose/src/androidMain/res/drawable/ic_*.xml
   ```

### Los colores no coinciden con Figma

Los colores están hardcodeados en los Vector Drawables. Para actualizarlos:
1. Abrir el archivo `.xml` correspondiente
2. Modificar el atributo `android:fillColor`
3. Republicar la SDK

---

**Última actualización**: 2026-01-26  
**Versión SDK**: 1.0.10  
**Fuente**: Figma Design System

