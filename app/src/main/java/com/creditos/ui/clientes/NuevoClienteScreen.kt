//NuevoClienteScreen.kt
package com.creditos.ui.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creditos.viewmodels.NuevoClienteViewModel
import com.creditos.data.entities.TipoDocumento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoClienteScreen(
    onNavigateBack: () -> Unit,
    viewModel: NuevoClienteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tiposDocumento by viewModel.tiposDocumento.collectAsState()
    val tiposDocumentoState by viewModel.tiposDocumentoState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var tipoDocumentoExpanded by remember { mutableStateOf(false) }
    var tipoDocumentoSeleccionado by remember { mutableStateOf<TipoDocumento?>(null) }
    var numeroDocumento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Campos para dirección
    var calle by remember { mutableStateOf("") }
    var numeroCalle by remember { mutableStateOf("") }
    var piso by remember { mutableStateOf("") }
    var puerta by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }

    // Manejar el estado de éxito
    LaunchedEffect(uiState) {
        if (uiState is NuevoClienteViewModel.UIState.Success) {
            kotlinx.coroutines.delay(500)
            onNavigateBack()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Cliente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (validarFormulario(nombre, apellido, tipoDocumentoSeleccionado, numeroDocumento, telefono, calle, ciudad, provincia)) {
                                viewModel.insertarCliente(
                                    nombre = nombre,
                                    apellido = apellido,
                                    tipoDocumentoId = tipoDocumentoSeleccionado!!.id,
                                    numeroDocumento = numeroDocumento,
                                    telefonoPrincipal = telefono,
                                    email = if (email.isNotBlank()) email else null,
                                    calle = calle,
                                    numeroCalle = numeroCalle,
                                    piso = piso,
                                    puerta = puerta,
                                    codigoPostal = codigoPostal,
                                    ciudad = ciudad,
                                    provincia = provincia
                                )
                            }
                        },
                        enabled = validarFormulario(nombre, apellido, tipoDocumentoSeleccionado, numeroDocumento, telefono, calle, ciudad, provincia) &&
                                uiState !is NuevoClienteViewModel.UIState.Loading
                    ) {
                        if (uiState is NuevoClienteViewModel.UIState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Guardar")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // CONTENIDO PRINCIPAL CON SCROLL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Mostrar error de tipos de documento si existe
            if (tiposDocumentoState is NuevoClienteViewModel.TiposDocumentoState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (tiposDocumentoState as NuevoClienteViewModel.TiposDocumentoState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Información Personal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información Personal",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nombre.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Apellido
                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = apellido.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tipo de Documento - MEJORADO
                    if (tiposDocumentoState is NuevoClienteViewModel.TiposDocumentoState.Loading) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cargando tipos de documento...")
                        }
                    } else if (tiposDocumento.isEmpty()) {
                        Text(
                            text = "No hay tipos de documento disponibles",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = tipoDocumentoExpanded,
                            onExpandedChange = { tipoDocumentoExpanded = !tipoDocumentoExpanded }
                        ) {
                            OutlinedTextField(
                                value = tipoDocumentoSeleccionado?.descripcion ?: "Seleccione tipo de documento",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tipo de Documento *") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoDocumentoExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                isError = tipoDocumentoSeleccionado == null && uiState is NuevoClienteViewModel.UIState.Error
                            )

                            ExposedDropdownMenu(
                                expanded = tipoDocumentoExpanded,
                                onDismissRequest = { tipoDocumentoExpanded = false },
                                modifier = Modifier.exposedDropdownSize() // Asegurar tamaño adecuado
                            ) {
                                tiposDocumento.forEach { tipo ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = tipo.descripcion,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        },
                                        onClick = {
                                            tipoDocumentoSeleccionado = tipo
                                            tipoDocumentoExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Número de Documento
                    OutlinedTextField(
                        value = numeroDocumento,
                        onValueChange = { numeroDocumento = it },
                        label = { Text("Número de Documento *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = numeroDocumento.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Teléfono
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = telefono.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Sección de Dirección
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Dirección Principal",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Calle
                    OutlinedTextField(
                        value = calle,
                        onValueChange = { calle = it },
                        label = { Text("Calle *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = calle.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Número
                    OutlinedTextField(
                        value = numeroCalle,
                        onValueChange = { numeroCalle = it },
                        label = { Text("Número") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Piso
                        OutlinedTextField(
                            value = piso,
                            onValueChange = { piso = it },
                            label = { Text("Piso") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        // Puerta
                        OutlinedTextField(
                            value = puerta,
                            onValueChange = { puerta = it },
                            label = { Text("Puerta") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Código Postal
                    OutlinedTextField(
                        value = codigoPostal,
                        onValueChange = { codigoPostal = it },
                        label = { Text("Código Postal") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ciudad
                        OutlinedTextField(
                            value = ciudad,
                            onValueChange = { ciudad = it },
                            label = { Text("Ciudad *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = ciudad.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                        )

                        // Provincia
                        OutlinedTextField(
                            value = provincia,
                            onValueChange = { provincia = it },
                            label = { Text("Provincia *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = provincia.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                        )
                    }
                }
            }

            // Mostrar error general si existe
            if (uiState is NuevoClienteViewModel.UIState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (uiState as NuevoClienteViewModel.UIState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Botón Guardar - con más espacio abajo para mejor scroll
            Button(
                onClick = {
                    if (validarFormulario(nombre, apellido, tipoDocumentoSeleccionado, numeroDocumento, telefono, calle, ciudad, provincia)) {
                        viewModel.insertarCliente(
                            nombre = nombre,
                            apellido = apellido,
                            tipoDocumentoId = tipoDocumentoSeleccionado!!.id,
                            numeroDocumento = numeroDocumento,
                            telefonoPrincipal = telefono,
                            email = if (email.isNotBlank()) email else null,
                            calle = calle,
                            numeroCalle = numeroCalle,
                            piso = piso,
                            puerta = puerta,
                            codigoPostal = codigoPostal,
                            ciudad = ciudad,
                            provincia = provincia
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = validarFormulario(nombre, apellido, tipoDocumentoSeleccionado, numeroDocumento, telefono, calle, ciudad, provincia) &&
                        uiState !is NuevoClienteViewModel.UIState.Loading
            ) {
                if (uiState is NuevoClienteViewModel.UIState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(16.dp)
                    )
                } else {
                    Text("Guardar Cliente")
                }
            }

            // Espacio extra al final para mejor scroll
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Función de validación
private fun validarFormulario(
    nombre: String,
    apellido: String,
    tipoDocumento: TipoDocumento?,
    numeroDocumento: String,
    telefono: String,
    calle: String,
    ciudad: String,
    provincia: String
): Boolean {
    return nombre.isNotBlank() &&
            apellido.isNotBlank() &&
            tipoDocumento != null &&
            numeroDocumento.isNotBlank() &&
            telefono.isNotBlank() &&
            calle.isNotBlank() &&
            ciudad.isNotBlank() &&
            provincia.isNotBlank()
}

@Preview(showBackground = true)
@Composable
fun NuevoClienteScreenPreview() {
    com.creditos.ui.theme.SistemaCreditosTheme {
        NuevoClienteScreen(
            onNavigateBack = {}
        )
    }
}