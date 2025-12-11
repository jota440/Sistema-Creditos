//CreditosApplication.kt
package com.creditos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.creditos.data.database.CreditosDatabase
import com.creditos.data.database.CsvDataLoader

@HiltAndroidApp
class CreditosApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("APP", "🚀 CreditosApplication iniciada")
        android.util.Log.d("APP", "ℹ️ Modo: ${if (BuildConfig.DEBUG) "DESARROLLO 🔧" else "PRODUCCIÓN 📦"}")

        // Cargar datos iniciales según el modo
        cargarDatosInicialesEnBackground()
    }

    private fun cargarDatosInicialesEnBackground() {
        applicationScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val db = CreditosDatabase.getInstance(applicationContext)

                    // Verificar si hay datos cargados
                    val paisCount = db.paisDao().count()

                    if (paisCount == 0) {
                        android.util.Log.d("APP", "📊 Tablas vacías detectadas")

                        if (BuildConfig.DEBUG) {
                            // 🔧 MODO DESARROLLO: Cargar CSV (permite backups)
                            cargarDatosDesdeCSV(db)
                        } else {
                            // 📦 MODO PRODUCCIÓN: La BD debería estar pre-poblada
                            // Si no lo está, cargar CSV como fallback
                            android.util.Log.w("APP", "⚠️ BD pre-poblada vacía, usando CSV como fallback")
                            cargarDatosDesdeCSV(db)
                        }
                    } else {
                        android.util.Log.d("APP", "✅ Datos ya cargados ($paisCount países)")
                        logDatabaseStats(db)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("APP", "❌ Error al cargar datos: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    private suspend fun cargarDatosDesdeCSV(db: CreditosDatabase) {
        val startTime = System.currentTimeMillis()

        android.util.Log.d("APP", "📂 Iniciando carga desde CSV...")

        CsvDataLoader.cargarDatosIniciales(
            applicationContext,
            db.openHelper.writableDatabase
        )

        val totalTime = System.currentTimeMillis() - startTime
        android.util.Log.d("APP", "✅ Datos CSV cargados en ${totalTime}ms")

        // Mostrar estadísticas finales
        logDatabaseStats(db)

        // En desarrollo, sugerir crear backup
        if (BuildConfig.DEBUG) {
            android.util.Log.d("APP", "")
            android.util.Log.d("APP", "💡 SUGERENCIA DE DESARROLLO:")
            android.util.Log.d("APP", "   Ve a Dev Settings y crea un backup")
            android.util.Log.d("APP", "   para restaurar rápidamente en próximas instalaciones")
            android.util.Log.d("APP", "")
        }
    }

    private suspend fun logDatabaseStats(db: CreditosDatabase) {
        try {
            val paises = db.paisDao().count()
            val provincias = db.provinciaDao().count()
            val codigosPostales = db.codigoPostalDao().count()
            val tiposDocumento = db.tipoDocumentoDao().obtenerActivos().size

            android.util.Log.d("APP", "")
            android.util.Log.d("APP", "📊 Estadísticas de la BD:")
            android.util.Log.d("APP", "   • Países: $paises")
            android.util.Log.d("APP", "   • Provincias: $provincias")
            android.util.Log.d("APP", "   • Códigos Postales: $codigosPostales")
            android.util.Log.d("APP", "   • Tipos Documento: $tiposDocumento")
            android.util.Log.d("APP", "")
        } catch (e: Exception) {
            android.util.Log.e("APP", "Error obteniendo estadísticas: ${e.message}")
        }
    }
}