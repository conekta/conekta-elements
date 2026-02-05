# buildSrc - Custom Gradle Tasks

Este directorio contiene tareas personalizadas de Gradle reutilizables en todos los módulos del proyecto.

## Estructura

```
buildSrc/
├── build.gradle.kts                              # Dependencias compartidas
├── README.md                                     # Esta documentación
└── src/main/kotlin/
    ├── ValidateStringsOrderTask.kt               # Valida orden alfabético
    └── ValidateStringsSpellingTask.kt            # Valida ortografía
```

## Tareas Disponibles

### ValidateStringsOrderTask

Valida que todos los string resources estén en orden alfabético.

**Ubicación**: `src/main/kotlin/ValidateStringsOrderTask.kt`

**Uso**:
```bash
./gradlew :compose:validateStringsOrder
```

**Ejemplo de registro**:
```kotlin
tasks.register<ValidateStringsOrderTask>("validateStringsOrder") {
    group = "verification"
    description = "Validates that all string resources are in alphabetical order"

    projectDirPath = project.projectDir
    stringsFiles = project.fileTree("src/commonMain/composeResources") {
        include("**/strings.xml")
    }
}
```

### ValidateStringsSpellingTask

Valida ortografía y gramática en string resources usando LanguageTool.

**Ubicación**: `src/main/kotlin/ValidateStringsSpellingTask.kt`

**Dependencias**: LanguageTool (ver `build.gradle.kts`)

**Uso**:
```bash
./gradlew :compose:validateStringsSpelling
```

**Ejemplo de registro**:
```kotlin
tasks.register<ValidateStringsSpellingTask>("validateStringsSpelling") {
    group = "verification"
    description = "Validates spelling and grammar in all string resources"

    projectDirPath = project.projectDir
    stringsFiles = project.fileTree("src/commonMain/composeResources") {
        include("**/strings.xml")
    }
    languageToolClasspath = configurations.getByName("languageTool")
}
```

## Crear una Nueva Tarea Personalizada

### 1. Agregar Dependencias (si es necesario)

En `buildSrc/build.gradle.kts`:
```kotlin
dependencies {
    implementation("org.example:library:1.0.0")
}
```

### 2. Crear la Clase de Tarea

Crear archivo `buildSrc/src/main/kotlin/MiTareaTask.kt`:

```kotlin
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class MiTareaTask : DefaultTask() {
    @get:InputFiles
    abstract var inputFiles: ConfigurableFileTree

    @get:Internal
    lateinit var projectDirPath: File

    @TaskAction
    fun execute() {
        // Lógica de la tarea aquí
        // NUNCA acceder a project.* aquí (rompe configuration cache)
    }
}
```

### 3. Registrar la Tarea

En el `build.gradle.kts` del módulo:
```kotlin
tasks.register<MiTareaTask>("miTarea") {
    group = "verification"
    description = "Descripción de lo que hace esta tarea"

    // Configurar propiedades en fase de configuración
    projectDirPath = project.projectDir
    inputFiles = project.fileTree("ruta/archivos") {
        include("**/*.ext")
    }
}
```

### 4. Probar la Tarea

```bash
# Ejecutar la tarea
./gradlew :module:miTarea

# Verificar configuration cache
./gradlew :module:miTarea  # Segunda ejecución debe reutilizar cache
```

## Reglas Importantes

### ✅ Hacer

- **Usar anotaciones apropiadas**: `@InputFiles`, `@Classpath`, `@Internal`
- **Configurar en fase de configuración**: Asignar propiedades fuera de `@TaskAction`
- **Un archivo por tarea**: `MiTareaTask.kt` contiene solo `MiTareaTask`
- **Extender DefaultTask**: Proporciona funcionalidad base de Gradle
- **Documentar**: Agregar javadoc/kdoc a la clase explicando qué hace

### ❌ No Hacer

- **NO acceder a `project.*` en `@TaskAction`**: Rompe configuration cache
- **NO usar tareas inline con `doLast`**: Difícil de mantener y no reutilizable
- **NO duplicar código**: Si dos módulos necesitan la misma tarea, créala aquí
- **NO olvidar las anotaciones**: Necesarias para que Gradle maneje inputs correctamente

## Configuration Cache

Todas las tareas en buildSrc están diseñadas para ser compatibles con configuration cache de Gradle, lo que mejora significativamente el rendimiento del build.

**Verificar compatibilidad**:
```bash
./gradlew :module:tarea --configuration-cache
```

**Mensajes de éxito**:
- Primera ejecución: `Configuration cache entry stored.`
- Ejecuciones posteriores: `Reusing configuration cache.`

## Beneficios de buildSrc

✨ **Código limpio**: build.gradle.kts más cortos y legibles
♻️ **Reutilizable**: Disponible en todos los módulos automáticamente
🔧 **Mantenible**: Un solo lugar para la lógica de cada tarea
🚀 **Performante**: Gradle cachea las clases compiladas
🧪 **Testeable**: Posibilidad de agregar tests unitarios
🎯 **Type-safe**: Kotlin DSL con verificación de tipos

## CI/CD

Las tareas de validación se ejecutan automáticamente en GitHub Actions:
- `validateStringsOrder` - Verifica orden alfabético
- `validateStringsSpelling` - Verifica ortografía y gramática

Ambas deben pasar para aprobar PRs.

## Referencias

- [Gradle Documentation - buildSrc](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources)
- [Gradle Documentation - Custom Tasks](https://docs.gradle.org/current/userguide/custom_tasks.html)
- [Gradle Documentation - Configuration Cache](https://docs.gradle.org/current/userguide/configuration_cache.html)
