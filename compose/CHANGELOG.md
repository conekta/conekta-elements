# Changelog - Conekta Elements Compose SDK

## [1.0.16] - 2026-01-26

### 🎨 Improved
- **Eliminados toasts de error redundantes**
  - Ya no se muestran toasts de error de validación
  - Los errores se muestran únicamente debajo de cada input
  - Experiencia de usuario más limpia y menos intrusiva
  - Feedback visual directo en el campo con error

### 🐛 Fixed
- **Validación más clara y directa**
  - Usuario ve exactamente qué campo tiene error
  - No hay mensajes duplicados (toast + mensaje debajo del input)
  - Mejora la usabilidad del formulario

---

## [1.0.15] - 2026-01-26

### ✨ Fixed
- **Ícono de check en modal "Tu pago está protegido" ahora usa el SVG correcto de Figma**
  - Extraído directamente del diseño de Figma usando MCP
  - Círculo verde (#16A34A) con check blanco (#FDFEFF)
  - Coincide 100% con el diseño original de Figma
  - Implementado como vector drawable nativo de Android

### 🎨 Improved
- **Calidad visual del modal mejorada**
  - Ícono vectorial de alta calidad (escalable sin pérdida)
  - Renderizado nativo en Android para mejor performance
  - Colores exactos del diseño de Figma

---

## [1.0.14] - 2026-01-26

### 🐛 Fixed
- **Modal "Tu pago está protegido" ahora usa iconos correctos**
  - Agregado botón de cerrar (✕) en la parte superior derecha
  - Icono de check (✓) verde en círculo ahora renderiza correctamente
  - Coincide 100% con el diseño de Figma del modal

### 🎨 Improved
- **Experiencia de usuario mejorada en modal**
  - Usuario puede cerrar el modal haciendo clic en el botón X
  - Iconos profesionales y consistentes con el diseño
  - Visual más limpio y claro

---

## [1.0.13] - 2026-01-26

### ✨ Added
- **Mensajes de error debajo de los inputs**
  - Agregado parámetro `errorMessage` a `ConektaTextField`
  - Los campos ahora muestran "Este dato es necesario" en rojo debajo cuando hay error
  - Los mensajes desaparecen automáticamente cuando el usuario empieza a escribir
  - Coincide con el diseño de Figma de validación visual completa

### 🎨 Improved
- **Experiencia de validación mejorada**
  - Feedback visual completo: borde rojo + mensaje de error
  - Mensajes claros y específicos para cada campo
  - UX profesional y consistente con patrones de diseño modernos

---

## [1.0.12] - 2026-01-26

### 🐛 Fixed
- **Lógica de iconos corregida (REVERTIDO)**
  - Los iconos ahora SOLO se muestran cuando se detecta la marca de tarjeta
  - Solo se muestra el icono de la marca detectada (no todos)
  - Coincide con el comportamiento esperado del diseño de Figma

### ✨ Added
- **Validación visual de campos**
  - Agregado parámetro `isError` a `ConektaTextField`
  - Los campos muestran borde rojo cuando hay errores de validación
  - Los errores se limpian automáticamente cuando el usuario empieza a escribir
  - Color `Error` agregado a `ConektaColors` (#DC2626)

### 🔄 Changed
- **Lógica de validación mejorada**
  - Ahora valida todos los campos al hacer clic en "Continuar"
  - Marca visualmente todos los campos con error (no solo el primero)
  - Mejora la UX al indicar claramente qué campos tienen problemas

---

## [1.0.11] - 2026-01-26

### 🐛 Fixed
- **Orientación de iconos corregida**
  - Logo de Visa ahora renderiza correctamente (aplicado flip vertical como en Figma)
  - Logo de Mastercard corregido (sin transformaciones)
  - Logo de Amex corregido (sin transformaciones)
  - Todos los iconos extraídos directamente de Figma MCP

### 🔄 Changed
- **Iconos siempre visibles**
  - Los tres iconos (Visa, Mastercard, Amex) ahora se muestran SIEMPRE
  - Ya no dependen de la detección de marca
  - Coincide 100% con el diseño de Figma

---

## [1.0.10] - 2026-01-26

### ✨ Added
- **Iconos de marcas de tarjeta** extraídos desde Figma
  - Logo de Visa (`ic_visa_card.xml`)
  - Logo de Mastercard (`ic_mastercard_card.xml`)
  - Logo de American Express (`ic_amex_card.xml`)
  - Logo de Conekta (`ic_conekta_logo.xml`)

- **Componentes específicos de plataforma** usando expect/actual pattern
  - `ConektaLogoImage()` - Renderiza el logo de Conekta
  - `CardBrandIcon()` - Renderiza icono de marca individual
  - `CardBrandIconsRow()` - Renderiza fila de iconos (todos o solo detectado)

### 🔄 Changed
- **ConektaTokenizer** ahora usa iconos reales en lugar de texto
  - Header muestra el logo de Conekta oficial
  - Campo de número de tarjeta muestra iconos de marcas
  - Iconos se muestran según la marca detectada

### 🏗️ Architecture
- Implementado patrón **expect/actual** para componentes específicos de plataforma
- Assets incluidos directamente en la SDK (Android resources)
- Soporte para resolución dinámica de recursos

### 📦 Assets
- Todos los assets son **Vector Drawables**
- Extraídos directamente del **Figma Design System**
- Tamaño total de assets: ~8KB
- Sin dependencias externas

### 🐛 Fixed
- Logo de Conekta ahora se renderiza correctamente
- Iconos de tarjetas se muestran al detectar la marca
- Mejoras en la detección de marcas de tarjetas

---

## [1.0.9] - 2026-01-26

### 🔧 Fixed
- Corrección de cursor en campos formateados usando `TextFieldValue`
- Mejoras en el formateo de número de tarjeta
- Corrección de orientación de iconos (Visa, Mastercard, Amex)

---

## [1.0.8] - 2026-01-26

### ✨ Added
- Modal "Tu pago está protegido" con `ModalBottomSheet`
- Soporte para drag-to-dismiss en modales
- Click outside to dismiss

---

## [1.0.0] - 2026-01-22

### 🎉 Initial Release
- **ConektaTokenizer** composable completo
- Tema personalizado de Conekta
- Validación de tarjetas
- Formateo automático de campos
- Detección de marcas de tarjetas
- Componentes personalizados (Button, TextField)
- Soporte para Android

---

## Próximas Versiones

### 🔮 Roadmap

#### v1.1.0 (Planeado)
- [ ] Soporte para iOS (recursos e implementación)
- [ ] Soporte para Web
- [ ] Animaciones de transición
- [ ] Temas personalizables

#### v1.2.0 (Planeado)
- [ ] Integración real con API de Conekta
- [ ] Manejo de errores de red
- [ ] Retry logic
- [ ] Loading states mejorados

#### v2.0.0 (Futuro)
- [ ] Soporte para MSI (Meses Sin Intereses)
- [ ] Métodos de pago adicionales
- [ ] Customización completa de UI
- [ ] Modo oscuro

---

**Nota**: Esta es una SDK en desarrollo activo. Los cambios pueden incluir breaking changes hasta la versión 2.0.0 estable.

Para más información, consulta:
- [README.md](README.md)
- [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)
- [ASSETS_README.md](ASSETS_README.md)

