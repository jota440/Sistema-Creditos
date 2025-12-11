// DevSettingsScreen.kt
package com.creditos.ui.dev

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.creditos.BuildConfig
import com.creditos.data.database.DatabaseDevHelper
import com.creditos.data.database.SqlScriptInstaller
import com.creditos.data.database.SqlScriptRunner
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var dbInfo by remember { mutableStateOf("Cargando...") }
    var hasPendingScripts by remember { mutableStateOf(false) }

    // Estado para el diálogo de confirmación de cada script
    var currentDialogScript by remember { mutableStateOf<File?>(null) }
    var currentDialogPreview by remember { mutableStateOf("") }
    var pendingDecision by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }

    // Cargar info inicial
    LaunchedEffect(Unit) {
        dbInfo = DatabaseDevHelper.getDatabaseInfo(context)

        // Sincronizar scripts desde assets → filesDir/querys
        SqlScriptInstaller.syncFromAssetsToFilesDir(context)
        hasPendingScripts = hasPendingSqlScripts(context)
    }

    if (!BuildConfig.DEBUG) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Esta pantalla solo está disponible en modo DEBUG")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes DB") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información de BD
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información de la BD",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = cleanDbInfoText(dbInfo),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Acciones (solo botones, sin título de sección)
            Card {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Ejecutar scripts SQL
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                message = ""

                                // Aseguramos tener los últimos scripts desde assets
                                SqlScriptInstaller.syncFromAssetsToFilesDir(context)

                                try {
                                    SqlScriptRunner.runPendingScripts(
                                        context = context,
                                        confirmExecution = { scriptFile, preview ->
                                            // Mostrar diálogo y esperar respuesta del usuario
                                            suspendCancellableCoroutine { cont ->
                                                currentDialogScript = scriptFile
                                                currentDialogPreview = preview
                                                pendingDecision = { accepted ->
                                                    if (cont.isActive) {
                                                        cont.resume(accepted)
                                                    }
                                                }
                                            }
                                        }
                                    )
                                    // Recalcular si quedan scripts pendientes
                                    hasPendingScripts = hasPendingSqlScripts(context)
                                    message = "✅ Proceso de scripts finalizado"
                                } catch (e: Exception) {
                                    message = "❌ Error ejecutando scripts: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && hasPendingScripts
                    ) {
                        Text("💾 Ejecutar Scripts")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón Parámetros
                    Button(
                        onClick = {
                            // Aquí podrás navegar a otra pantalla o abrir un diálogo
                            message = "Pantalla de parámetros pendiente de implementar"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text("🛠️ Parámetros")
                    }
                }
            }

            // Mensajes (éxito / error / info)
            if (message.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            message.contains("✅") -> MaterialTheme.colorScheme.primaryContainer
                            message.contains("❌") -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Indicador de carga
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        // Diálogo de confirmación para cada script
        if (currentDialogScript != null) {
            AlertDialog(
                onDismissRequest = { /* obligamos a elegir explícitamente */ },
                title = {
                    Text("¿Ejecutar script ${currentDialogScript!!.name}?")
                },
                text = {
                    Text(
                        text = currentDialogPreview.ifBlank { "(Script vacío o muy corto)" },
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        pendingDecision?.invoke(true)
                        currentDialogScript = null
                        currentDialogPreview = ""
                        pendingDecision = null
                    }) {
                        Text("Sí, ejecutar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        pendingDecision?.invoke(false)
                        currentDialogScript = null
                        currentDialogPreview = ""
                        pendingDecision = null
                    }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

// Quita el encabezado "📊 Información de la BD:" si viene así desde DatabaseDevHelper
private fun cleanDbInfoText(raw: String): String {
    return raw
        .replace("📊 Información de la BD:", "")
        .trim()
}

// Comprueba si hay scripts .sql pendientes en filesDir/querys
private fun hasPendingSqlScripts(context: android.content.Context): Boolean {
    val scriptsDir = File(context.filesDir, "querys")
    val pending = scriptsDir.listFiles { f ->
        f.isFile && f.extension.equals("sql", ignoreCase = true)
    } ?: return false
    return pending.isNotEmpty()
}
