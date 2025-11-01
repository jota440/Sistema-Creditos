package com.creditos.ui.pagos

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
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
import com.creditos.viewmodels.RegistrarPagoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarPagoScreen(
    onNavigateBack: () -> Unit,
    viewModel: RegistrarPagoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var monto by remember { mutableStateOf("") }
    var metodoPagoExpanded by remember { mutableStateOf(false) }
    var metodoPagoSeleccionado by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var concepto by remember { mutableStateOf("CUOTA") }

    val metodosPago = listOf("EFECTIVO", "TRANSFERENCIA", "TARJETA", "CHEQUE")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Pago") },
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
            // Información del Préstamo (placeholder por ahora)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información del Préstamo",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cliente:")
                        Text("Juan Pérez", fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Préstamo:")
                        Text("€1,000.00", fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Saldo Pendiente:")
                        Text("€750.00", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Cuotas Pendientes (placeholder)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Cuotas Pendientes",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    CuotaItem(
                        numero = 3,
                        fecha = "2024-02-15",
                        monto = "€250.00",
                        seleccionada = true
                    )

                    CuotaItem(
                        numero = 4,
                        fecha = "2024-03-15",
                        monto = "€250.00",
                        seleccionada = false
                    )

                    CuotaItem(
                        numero = 5,
                        fecha = "2024-04-15",
                        monto = "€250.00",
                        seleccionada = false
                    )
                }
            }

            // Datos del Pago
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Datos del Pago",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Monto
                    OutlinedTextField(
                        value = monto,
                        onValueChange = {
                            // Permitir solo números y punto decimal
                            if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                monto = it
                            }
                        },
                        label = { Text("Monto del Pago") },
                        leadingIcon = {
                            Icon(Icons.Default.AttachMoney, contentDescription = null)
                        },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Método de Pago
                    ExposedDropdownMenuBox(
                        expanded = metodoPagoExpanded,
                        onExpandedChange = { metodoPagoExpanded = !metodoPagoExpanded }
                    ) {
                        OutlinedTextField(
                            value = metodoPagoSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Método de Pago") },
                            leadingIcon = {
                                Icon(Icons.Default.Payment, contentDescription = null)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = metodoPagoExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = metodoPagoExpanded,
                            onDismissRequest = { metodoPagoExpanded = false }
                        ) {
                            metodosPago.forEach { metodo ->
                                DropdownMenuItem(
                                    text = { Text(metodo) },
                                    onClick = {
                                        metodoPagoSeleccionado = metodo
                                        metodoPagoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Referencia
                    OutlinedTextField(
                        value = referencia,
                        onValueChange = { referencia = it },
                        label = { Text("Referencia (opcional)") },
                        leadingIcon = {
                            Icon(Icons.Default.CreditCard, contentDescription = null)
                        },
                        placeholder = {
                            Text(
                                when (metodoPagoSeleccionado) {
                                    "TRANSFERENCIA" -> "Número de transacción"
                                    "TARJETA" -> "Últimos 4 dígitos"
                                    "CHEQUE" -> "Número de cheque"
                                    else -> "Referencia"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Concepto
                    ExposedDropdownMenuBox(
                        expanded = false, // Simplificado por ahora
                        onExpandedChange = {}
                    ) {
                        OutlinedTextField(
                            value = concepto,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Concepto") },
                            leadingIcon = {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Fecha de Pago (automática)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fecha de pago: ${obtenerFechaActual()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Resumen del Pago
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Resumen del Pago",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    InfoResumen(
                        label = "Monto a Pagar:",
                        value = if (monto.isNotBlank()) "€$monto" else "€0.00",
                        esImportante = true
                    )

                    InfoResumen(
                        label = "Método:",
                        value = metodoPagoSeleccionado.ifEmpty { "No seleccionado" }
                    )

                    if (referencia.isNotBlank()) {
                        InfoResumen(
                            label = "Referencia:",
                            value = referencia
                        )
                    }

                    InfoResumen(
                        label = "Concepto:",
                        value = concepto
                    )
                }
            }

            // Botón Registrar Pago
            Button(
                onClick = {
                    if (validarFormulario(monto, metodoPagoSeleccionado)) {
                        viewModel.registrarPago(
                            monto = monto.toDouble(),
                            metodoPago = metodoPagoSeleccionado,
                            referencia = referencia.ifEmpty { null },
                            concepto = concepto
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = validarFormulario(monto, metodoPagoSeleccionado)
            ) {
                if (uiState is RegistrarPagoViewModel.UIState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(16.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Registrar Pago")
                }
            }

            // Manejar estado de éxito
            if (uiState is RegistrarPagoViewModel.UIState.Success) {
                onNavigateBack()
            }
        }
    }
}

@Composable
fun CuotaItem(
    numero: Int,
    fecha: String,
    monto: String,
    seleccionada: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Cuota #$numero",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Vence: $fecha",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = monto,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (seleccionada) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun InfoResumen(
    label: String,
    value: String,
    esImportante: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (esImportante) FontWeight.Bold else FontWeight.Normal,
            color = if (esImportante) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun obtenerFechaActual(): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
}

private fun validarFormulario(monto: String, metodoPago: String): Boolean {
    return monto.isNotBlank() && monto.toDoubleOrNull() != null && monto.toDouble() > 0 &&
            metodoPago.isNotBlank()
}

@Composable
@Preview(showBackground = true)
fun RegistrarPagoScreenPreview() {
    com.creditos.ui.theme.SistemaCreditosTheme {
        RegistrarPagoScreen(
            onNavigateBack = {}
        )
    }
}