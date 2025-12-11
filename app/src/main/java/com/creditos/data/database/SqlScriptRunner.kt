// SqlScriptRunner.kt
package com.creditos.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SqlScriptRunner {

    private const val SCRIPTS_DIR_NAME = "querys"
    private const val EXECUTED_DIR_NAME = "ejecutados"
    private const val DATABASE_NAME = "creditos.db" // cambia si tu BD se llama distinto

    /**
     * Ejecuta todos los scripts .sql pendientes en orden por nombre de archivo.
     *
     * @param context Contexto de la app
     * @param confirmExecution callback que debe devolver true si el usuario acepta ejecutar el script.
     *                         Recibe el fichero y un pequeño preview del contenido.
     */

    suspend fun runPendingScripts(
        context: Context,
        confirmExecution: suspend (scriptFile: File, preview: String) -> Boolean
    ) = withContext(Dispatchers.IO) {
        val scriptsDir = File(context.filesDir, SCRIPTS_DIR_NAME)
        val executedDir = File(scriptsDir, EXECUTED_DIR_NAME)

        if (!scriptsDir.exists()) scriptsDir.mkdirs()
        if (!executedDir.exists()) executedDir.mkdirs()

        val pendingFiles = scriptsDir.listFiles { f ->
            f.isFile && f.extension.equals("sql", ignoreCase = true)
        }?.sortedBy { it.name.lowercase(Locale.ROOT) } ?: emptyList()

        if (pendingFiles.isEmpty()) return@withContext

        val dbFile = context.getDatabasePath(DATABASE_NAME)
        if (!dbFile.exists()) {
            // No hay BD aún, no se puede ejecutar nada
            return@withContext
        }

        val database = SQLiteDatabase.openDatabase(
            dbFile.path,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )

        try {
            for (file in pendingFiles) {
                val content = file.readText(Charsets.UTF_8)

                val preview = content.lineSequence()
                    .take(5)
                    .joinToString("\n")

                val shouldRun = confirmExecution(file, preview)

                // Timestamp compatible con API 24
                val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT)
                val timestamp = formatter.format(Date())

                val newName = "${file.nameWithoutExtension}__${timestamp}.sql"
                val executedFile = File(executedDir, newName)

                if (shouldRun) {
                    database.beginTransaction()
                    try {
                        val statements = splitSqlStatements(content)
                        for (stmt in statements) {
                            if (stmt.isNotBlank()) {
                                database.execSQL(stmt)
                            }
                        }
                        database.setTransactionSuccessful()
                    } finally {
                        database.endTransaction()
                    }
                }

                // Mover siempre a ejecutados (tanto si se ejecutó como si no)
                file.copyTo(executedFile, overwrite = false)
                file.delete()
            }
        } finally {
            database.close()
        }
    }

    private fun splitSqlStatements(sqlText: String): List<String> {
        return sqlText
            .split(';')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
