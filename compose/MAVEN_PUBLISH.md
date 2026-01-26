# Maven Local Publishing

## Publicación Exitosa ✅

El módulo `compose` de Conekta Elements ha sido publicado exitosamente en Maven Local.

### Coordenadas Maven

```gradle
implementation("io.conekta.elements:compose-android:1.0.10")
```

### Repositorio

```kotlin
repositories {
    mavenLocal()
    google()
    mavenCentral()
}
```

### Archivos Publicados

La librería se encuentra en:
```
~/.m2/repository/io/conekta/elements/compose-android/1.0.10/
```

Archivos generados:
- `compose-android-1.0.10.aar` - Librería Android
- `compose-android-1.0.10.pom` - Descriptor Maven
- `compose-android-1.0.10.module` - Gradle Module Metadata
- `compose-android-1.0.10-sources.jar` - Código fuente

### Dependencias

El módulo `compose` depende de:
- `io.conekta.elements:shared-android:1.0.10`
- Jetpack Compose UI 1.10.0
- Material3 1.9.0
- Lifecycle Compose 2.9.6

### iOS Deshabilitado Temporalmente

Para facilitar la publicación y pruebas iniciales, los targets de iOS han sido comentados en `build.gradle.kts`:
- `iosX64`
- `iosArm64`
- `iosSimulatorArm64`

Estos pueden ser re-habilitados cuando se necesite soporte iOS.

### Comandos de Publicación

```bash
# Publicar el módulo compose
cd /Users/mauricio/projects/conekta-elements
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :compose:publishToMavenLocal

# Publicar el módulo shared (requerido por compose)
./gradlew :shared:publishToMavenLocal
```

### Integración en Proyectos

Ver `conekta-demo-simple` para un ejemplo de integración:
- Agregar `mavenLocal()` en `settings.gradle.kts`
- Agregar dependencia en `app/build.gradle.kts`
- Usar `ConektaTokenizer` composable

### Próximos Pasos

1. ✅ Publicación en Maven Local
2. ✅ Integración en app de ejemplo
3. ⏳ Pruebas funcionales
4. ⏳ Re-habilitar soporte iOS
5. ⏳ Publicación en Maven Central

---

**Fecha de publicación**: 2026-01-22
**Versión**: 1.0.10
**Estado**: Android only (iOS deshabilitado temporalmente)

