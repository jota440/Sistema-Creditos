package com.creditos.ui.prestamos

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creditos.viewmodels.NuevoPrestamoViewModel
import com.creditos.data.entities.Cliente

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPrestamoScreen(
    onNavigateBack: () -> Unit,
    viewModel: NuevoPrestamoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clientes by viewModel.clientes.collectAsState()

    var clienteSeleccionado by remember { mutableStateOf<Cliente?>(null) }
    var clienteExpanded by remember { mutableStateOf(false) }

    var montoPrincipal by remember { mutableStateOf("") }
    var tasaInteres by remember { mutableStateOf("") }
    var numeroCuotas by remember { mutableStateOf("") }

    var frecuenciaExpanded by remember { mutableStateOf(false) }
    var frecuenciaSeleccionada by remember { mutableStateOf("MENSUAL") }

    var amortizacionExpanded by remember { mutableStateOf(false) }
    var amortizacionSeleccionada by remember { mutableStateOf("FRANCES") }

    var fechaInicio by remember { mutableStateOf(obtenerFechaActual()) }
    var notas by remember { mutableStateOf("") }

    val frecuencias = listOf("SEMANAL", "QUINCENAL", "MENSUAL", "ANUAL")
    val amortizaciones = listOf("FRANCES", "ALEMAN", "AMERICANO")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Préstamo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
            // Selección de Cliente
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Seleccionar Cliente",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = clienteExpanded,
                        onExpandedChange = { clienteExpanded = !clienteExpanded }
                    ) {
                        OutlinedTextField(
                            value = clienteSeleccionado?.let { "${it.nombre} ${it.apellido}" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Cliente") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = clienteExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = clienteExpanded,
                            onDismissRequest = { clienteExpanded = false }
                        ) {
                            clientes.forEach { cliente ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${cliente.nombre} ${cliente.apellido} - ${cliente.numeroDocumento}")
                                    },
                                    onClick = {
                                        clienteSeleccionado = cliente
                                        clienteExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Datos del Préstamo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Datos del Préstamo",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Monto Principal
                    OutlinedTextField(
                        value = montoPrincipal,
                        onValueChange = {
                            if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                montoPrincipal = it
                            }
                        },
                        label = { Text("Monto Principal") },
                        leadingIcon = {
                            Icon(Icons.Default.AttachMoney, contentDescription = null)
                        },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tasa de Interés
                    OutlinedTextField(
                        value = tasaInteres,
                        onValueChange = {
                            if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                tasaInteres = it
                            }
                        },
                        label = { Text("Tasa de Interés Anual (%)") },
                        leadingIcon = {
                            Icon(Icons.Default.AttachMoney, contentDescription = null)
                        },
                        placeholder = { Text("12.0") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Número de Cuotas
                    OutlinedTextField(
                        value = numeroCuotas,
                        onValueChange = {
                            if (it.matches(Regex("^\\d*$"))) {
                                numeroCuotas = it
                            }
                        },
                        label = { Text("Número de Cuotas") },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        },
                        placeholder = { Text("12") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Frecuencia de Pago
                    ExposedDropdownMenuBox(
                        expanded = frecuenciaExpanded,
                        onExpandedChange = { frecuenciaExpanded = !frecuenciaExpanded }
                    ) {
                        OutlinedTextField(
                            value = when (frecuenciaSeleccionada) {
                                "SEMANAL" -> "Semanal"
                                "QUINCENAL" -> "Quincenal"
                                "MENSUAL" -> "Mensual"
                                "ANUAL" -> "Anual"
                                else -> frecuenciaSeleccionada
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Frecuencia de Pago") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = frecuenciaExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = frecuenciaExpanded,
                            onDismissRequest = { frecuenciaExpanded = false }
                        ) {
                            frecuencias.forEach { frecuencia ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (frecuencia) {
                                                "SEMANAL" -> "Semanal"
                                                "QUINCENAL" -> "Quincenal"
                                                "MENSUAL" -> "Mensual"
                                                "ANUAL" -> "Anual"
                                                else -> frecuencia
                                            }
                                        )
                                    },
                                    onClick = {
                                        frecuenciaSeleccionada = frecuencia
                                        frecuenciaExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tipo de Amortización
                    ExposedDropdownMenuBox(
                        expanded = amortizacionExpanded,
                        onExpandedChange = { amortizacionExpanded = !amortizacionExpanded }
                    ) {
                        OutlinedTextField(
                            value = when (amortizacionSeleccionada) {
                                "FRANCES" -> "Francés (Cuota Fija)"
                                "ALEMAN" -> "Alemán (Capital Fijo)"
                                "AMERICANO" -> "Americano (Balloon)"
                                else -> amortizacionSeleccionada
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Amortización") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = amortizacionExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = amortizacionExpanded,
                            onDismissRequest = { amortizacionExpanded = false }
                        ) {
                            amortizaciones.forEach { amortizacion ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (amortizacion) {
                                                "FRANCES" -> "Francés (Cuota Fija)"
                                                "ALEMAN" -> "Alemán (Capital Fijo)"
                                                "AMERICANO" -> "Americano (Balloon)"
                                                else -> amortizacion
                                            }
                                        )
                                    },
                                    onClick = {
                                        amortizacionSeleccionada = amortizacion
                                        amortizacionExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fecha de Inicio
                    OutlinedTextField(
                        value = fechaInicio,
                        onValueChange = { fechaInicio = it },
                        label = { Text("Fecha de Inicio") },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Notas
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        label = { Text("Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            }

            // Botón Calcular
            Button(
                onClick = {
                    // Aquí irá la lógica de cálculo
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = validarDatosCalculo(
                    clienteSeleccionado,
                    montoPrincipal,
                    tasaInteres,
                    numeroCuotas
                )
            ) {
                Icon(
                    Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Calcular Amortización")
            }

            // Botón Guardar
            Button(
                onClick = {
                    if (validarDatosCompletos(
                            clienteSeleccionado,
                            montoPrincipal,
                            tasaInteres,
                            numeroCuotas
                        )) {
                        viewModel.crearPrestamo(
                            clienteId = clienteSeleccionado!!.id,
                            montoPrincipal = montoPrincipal.toDouble(),
                            tasaInteres = tasaInteres.toDouble(),
                            numeroCuotas = numeroCuotas.toInt(),
                            frecuenciaPago = frecuenciaSeleccionada,
                            tipoAmortizacion = amortizacionSeleccionada,
                            fechaInicio = fechaInicio,
                            notas = if (notas.isNotBlank()) notas else null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = validarDatosCompletos(
                    clienteSeleccionado,
                    montoPrincipal,
                    tasaInteres,
                    numeroCuotas
                )
            ) {
                if (uiState is NuevoPrestamoViewModel.UIState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(16.dp)
                    )
                } else {
                    Text("Guardar Préstamo")
                }
            }

            // Manejar estado de éxito
            if (uiState is NuevoPrestamoViewModel.UIState.Success) {
                onNavigateBack()
            }
        }
    }
}

private fun obtenerFechaActual(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

private fun validarDatosCalculo(
    cliente: Cliente?,
    monto: String,
    tasa: String,
    cuotas: String
): Boolean {
    return cliente != null &&
            monto.isNotBlank() && monto.toDoubleOrNull() != null && monto.toDouble() > 0 &&
            tasa.isNotBlank() && tasa.toDoubleOrNull() != null && tasa.toDouble() >= 0 &&
            cuotas.isNotBlank() && cuotas.toIntOrNull() != null && cuotas.toInt() > 0
}

private fun validarDatosCompletos(
    cliente: Cliente?,
    monto: String,
    tasa: String,
    cuotas: String
): Boolean {
    return validarDatosCalculo(cliente, monto, tasa, cuotas)
}

@Preview(showBackground = true)
@Composable
fun NuevoPrestamoScreenPreview() {
    com.creditos.ui.theme.SistemaCreditosTheme {
        NuevoPrestamoScreen(
            onNavigateBack = {}
        )
    }
}