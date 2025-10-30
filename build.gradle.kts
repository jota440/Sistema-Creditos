// Archivo de configuración de build de nivel superior (Root Project)
// Aquí se declaran los plugins y sus versiones, pero NO se aplican a los módulos ('apply false')
// para que puedan ser usados por alias en los módulos individuales.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // KSP actualizado para Kotlin 2.0.21
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false

    id("com.google.dagger.hilt.android") version "2.50" apply false

    // Plugin de Compose para Kotlin 2.0+
    alias(libs.plugins.kotlin.compose) apply false
}
