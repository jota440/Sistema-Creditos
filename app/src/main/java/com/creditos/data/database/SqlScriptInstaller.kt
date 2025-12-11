//SqlScriptInstaller.kt
package com.creditos.data.database

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

object SqlScriptInstaller {

    private const val SCRIPTS_DIR_NAME = "querys"
    private const val EXECUTED_DIR_NAME = "ejecutados"

    /**
     * Copia desde assets/querys todos los .sql que:
     *  - no estén ya en filesDir/querys
     *  - y no tengan ya una versión ejecutada en filesDir/querys/ejecutados
     */
    suspend fun syncFromAssetsToFilesDir(context: Context) = withContext(Dispatchers.IO) {
        val scriptsDir = File(context.filesDir, SCRIPTS_DIR_NAME)
        val executedDir = File(scriptsDir, EXECUTED_DIR_NAME)

        if (!scriptsDir.exists()) scriptsDir.mkdirs()
        if (!executedDir.exists()) executedDir.mkdirs()

        val assetManager = context.assets

        val assetFiles = assetManager.list("querys")
            ?.filter { it.lowercase(Locale.ROOT).endsWith(".sql") }
            ?: emptyList()

        // Nombres base de scripts que YA han sido ejecutados
        // Formato ejecutado: nombreSinExt__yyyyMMdd_HHmmss.sql
        val executedBaseNames: Set<String> = executedDir.listFiles()
            ?.filter { it.isFile && it.name.lowercase(Locale.ROOT).endsWith(".sql") }
            ?.mapNotNull { file ->
                val nameWithoutExt = file.name.substringBeforeLast('.')
                // coge la parte antes de "__"
                nameWithoutExt.substringBefore("__")
            }
            ?.toSet()
            ?: emptySet()

        for (assetName in assetFiles) {
            val targetFile = File(scriptsDir, assetName)

            val baseNameNoExt = assetName.substringBeforeLast('.')
            val alreadyExecuted = executedBaseNames.contains(baseNameNoExt)

            // Si ya está pendiente en querys o ya se ejecutó alguna vez → lo saltamos
            if (targetFile.exists() || alreadyExecuted) continue

            // Copiamos desde assets/querys/assetName → filesDir/querys/assetName
            assetManager.open("querys/$assetName").use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}
