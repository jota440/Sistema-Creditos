//DatabaseDevHelper.kt
package com.creditos.data.database

import android.content.Context
import com.creditos.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🛠️ Utilidad para gestionar la BD durante el desarrollo
 *
 * Funcionalidades:
 * 1. Crear backup de BD con datos cargados
 * 2. Restaurar backup rápidamente
 * 3. Exportar BD actual para incluir en assets
 */
object DatabaseDevHelper {

    private const val DB_NAME = "creditos.db"
    private const val BACKUP_DIR = "db_backups"

    /**
     * 💾 Crear backup de la BD actual
     * Útil después de cargar los CSV por primera vez
     */
    suspend fun createBackup(context: Context, name: String = "backup"): File? = withContext(Dispatchers.IO) {
        if (!BuildConfig.DEBUG) {
            android.util.Log.w("DB_DEV", "⚠️ Backup solo disponible en DEBUG")
            return@withContext null
        }

        try {
            val dbPath = context.getDatabasePath(DB_NAME)
            if (!dbPath.exists()) {
                android.util.Log.e("DB_DEV", "❌ BD no existe en ${dbPath.path}")
                return@withContext null
            }

            // Crear directorio de backups
            val backupDir = File(context.filesDir, BACKUP_DIR)
            backupDir.mkdirs()

            // Nombre con timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val backupFile = File(backupDir, "${name}_$timestamp.db")

            // Copiar BD
            FileInputStream(dbPath).use { input ->
                FileOutputStream(backupFile).use { output ->
                    input.copyTo(output)
                }
            }

            val sizeMB = backupFile.length() / (1024.0 * 1024.0)
            android.util.Log.d("DB_DEV", "✅ Backup creado: ${backupFile.name} (${String.format("%.2f", sizeMB)}MB)")
            android.util.Log.d("DB_DEV", "📁 Ubicación: ${backupFile.absolutePath}")

            return@withContext backupFile
        } catch (e: Exception) {
            android.util.Log.e("DB_DEV", "❌ Error creando backup: ${e.message}", e)
            return@withContext null
        }
    }

    /**
     * 📥 Restaurar backup más reciente
     * Mucho más rápido que cargar CSV
     */
    suspend fun restoreLatestBackup(context: Context): Boolean = withContext(Dispatchers.IO) {
        if (!BuildConfig.DEBUG) {
            android.util.Log.w("DB_DEV", "⚠️ Restore solo disponible en DEBUG")
            return@withContext false
        }

        try {
            val backupDir = File(context.filesDir, BACKUP_DIR)
            if (!backupDir.exists()) {
                android.util.Log.e("DB_DEV", "❌ No hay backups disponibles")
                return@withContext false
            }

            // Obtener backup más reciente
            val backups = backupDir.listFiles { file -> file.extension == "db" }
            if (backups.isNullOrEmpty()) {
                android.util.Log.e("DB_DEV", "❌ No se encontraron archivos de backup")
                return@withContext false
            }

            val latestBackup = backups.maxByOrNull { it.lastModified() }
            if (latestBackup == null) {
                android.util.Log.e("DB_DEV", "❌ No se pudo determinar el backup más reciente")
                return@withContext false
            }

            android.util.Log.d("DB_DEV", "📥 Restaurando backup: ${latestBackup.name}")

            // Cerrar BD si está abierta
            CreditosDatabase.resetDatabaseForDevelopment(context)

            // Copiar backup a ubicación de BD
            val dbPath = context.getDatabasePath(DB_NAME)
            dbPath.parentFile?.mkdirs()

            FileInputStream(latestBackup).use { input ->
                FileOutputStream(dbPath).use { output ->
                    input.copyTo(output)
                }
            }

            android.util.Log.d("DB_DEV", "✅ BD restaurada desde backup")
            return@withContext true
        } catch (e: Exception) {
            android.util.Log.e("DB_DEV", "❌ Error restaurando backup: ${e.message}", e)
            return@withContext false
        }
    }

    /**
     * 📤 Exportar BD actual a assets para distribución
     * Después de crear este archivo, muévelo manualmente a:
     * app/src/main/assets/databases/creditos_prepopulated.db
     */
    suspend fun exportToAssets(context: Context): File? = withContext(Dispatchers.IO) {
        if (!BuildConfig.DEBUG) {
            android.util.Log.w("DB_DEV", "⚠️ Export solo disponible en DEBUG")
            return@withContext null
        }

        try {
            val dbPath = context.getDatabasePath(DB_NAME)
            if (!dbPath.exists()) {
                android.util.Log.e("DB_DEV", "❌ BD no existe")
                return@withContext null
            }

            // Exportar a directorio de descargas o cache
            val exportFile = File(context.getExternalFilesDir(null), "creditos_prepopulated.db")

            FileInputStream(dbPath).use { input ->
                FileOutputStream(exportFile).use { output ->
                    input.copyTo(output)
                }
            }

            val sizeMB = exportFile.length() / (1024.0 * 1024.0)
            android.util.Log.d("DB_DEV", "✅ BD exportada: ${exportFile.name} (${String.format("%.2f", sizeMB)}MB)")
            android.util.Log.d("DB_DEV", "📁 Ubicación: ${exportFile.absolutePath}")
            android.util.Log.d("DB_DEV", "")
            android.util.Log.d("DB_DEV", "📋 SIGUIENTE PASO:")
            android.util.Log.d("DB_DEV", "   1. Copia el archivo desde: ${exportFile.absolutePath}")
            android.util.Log.d("DB_DEV", "   2. Pégalo en: app/src/main/assets/databases/creditos_prepopulated.db")
            android.util.Log.d("DB_DEV", "   3. Cambia BuildConfig.DEBUG check en CreditosDatabase.getInstance()")

            return@withContext exportFile
        } catch (e: Exception) {
            android.util.Log.e("DB_DEV", "❌ Error exportando BD: ${e.message}", e)
            return@withContext null
        }
    }

    /**
     * 📊 Información de la BD actual
     */
    suspend fun getDatabaseInfo(context: Context): String = withContext(Dispatchers.IO) {
        val dbPath = context.getDatabasePath(DB_NAME)

        if (!dbPath.exists()) {
            return@withContext "❌ BD no existe"
        }

        val sizeMB = dbPath.length() / (1024.0 * 1024.0)
        val lastModified = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(dbPath.lastModified()))

        return@withContext """
            📊 Información de la BD:
            - Tamaño: ${String.format("%.2f", sizeMB)}MB
            - Última modificación: $lastModified
            - Ubicación: ${dbPath.absolutePath}
        """.trimIndent()
    }

    /**
     * 📋 Listar backups disponibles
     */
    suspend fun listBackups(context: Context): List<String> = withContext(Dispatchers.IO) {
        val backupDir = File(context.filesDir, BACKUP_DIR)
        if (!backupDir.exists()) {
            return@withContext emptyList()
        }

        val backups = backupDir.listFiles { file -> file.extension == "db" }
        backups?.sortedByDescending { it.lastModified() }?.map { file ->
            val sizeMB = file.length() / (1024.0 * 1024.0)
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(file.lastModified()))
            "${file.name} - ${String.format("%.2f", sizeMB)}MB - $date"
        } ?: emptyList()
    }
}
