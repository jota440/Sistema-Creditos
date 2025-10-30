// settings.gradle.kts

pluginManagement {
    repositories {
        // SIMPLIFICAR: Eliminar el bloque 'content'
        google()

        mavenCentral()
        gradlePluginPortal()
    }
}

// El resto del archivo queda igual
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SistemaCreditos"
include(":app")