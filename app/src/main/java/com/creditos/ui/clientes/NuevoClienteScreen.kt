package com.creditos.ui.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var tipoDocumentoExpanded by remember { mutableStateOf(false) }
    var tipoDocumentoSeleccionado by remember { mutableStateOf<TipoDocumento?>(null) }
    var numeroDocumento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

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
                            if (nombre.isNotBlank() && apellido.isNotBlank() &&
                                tipoDocumentoSeleccionado != null &&
                                numeroDocumento.isNotBlank() && telefono.isNotBlank()) {

                                viewModel.insertarCliente(
                                    nombre = nombre,
                                    apellido = apellido,
                                    tipoDocumentoId = tipoDocumentoSeleccionado!!.id,
                                    numeroDocumento = numeroDocumento,
                                    telefonoPrincipal = telefono,
                                    email = if (email.isNotBlank()) email else null
                                )
                            }
                        },
                        enabled = nombre.isNotBlank() && apellido.isNotBlank() &&
                                tipoDocumentoSeleccionado != null &&
                                numeroDocumento.isNotBlank() && telefono.isNotBlank()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
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
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Apellido
                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tipo de Documento
                    ExposedDropdownMenuBox(
                        expanded = tipoDocumentoExpanded,
                        onExpandedChange = { tipoDocumentoExpanded = !tipoDocumentoExpanded }
                    ) {
                        OutlinedTextField(
                            value = tipoDocumentoSeleccionado?.descripcion ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Documento") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoDocumentoExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = tipoDocumentoExpanded,
                            onDismissRequest = { tipoDocumentoExpanded = false }
                        ) {
                            tiposDocumento.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo.descripcion) },
                                    onClick = {
                                        tipoDocumentoSeleccionado = tipo
                                        tipoDocumentoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Número de Documento
                    OutlinedTextField(
                        value = numeroDocumento,
                        onValueChange = { numeroDocumento = it },
                        label = { Text("Número de Documento") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Teléfono
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
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

            // Botón Guardar
            Button(
                onClick = {
                    if (nombre.isNotBlank() && apellido.isNotBlank() &&
                        tipoDocumentoSeleccionado != null &&
                        numeroDocumento.isNotBlank() && telefono.isNotBlank()) {

                        viewModel.insertarCliente(
                            nombre = nombre,
                            apellido = apellido,
                            tipoDocumentoId = tipoDocumentoSeleccionado!!.id,
                            numeroDocumento = numeroDocumento,
                            telefonoPrincipal = telefono,
                            email = if (email.isNotBlank()) email else null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = nombre.isNotBlank() && apellido.isNotBlank() &&
                        tipoDocumentoSeleccionado != null &&
                        numeroDocumento.isNotBlank() && telefono.isNotBlank()
            ) {
                if (uiState is NuevoClienteViewModel.UIState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(16.dp)
                    )
                } else {
                    Text("Guardar Cliente")
                }
            }

            // Manejar estado de éxito
            if (uiState is NuevoClienteViewModel.UIState.Success) {
                onNavigateBack()
            }
        }
    }
}
