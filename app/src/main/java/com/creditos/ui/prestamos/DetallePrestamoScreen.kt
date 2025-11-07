@file:OptIn(ExperimentalMaterial3Api::class)
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
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creditos.data.entities.Prestamo
import com.creditos.viewmodels.DetallePrestamoViewModel

@Composable
fun DetallePrestamoScreen(
    prestamoId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToRegistrarPago: () -> Unit,
    viewModel: DetallePrestamoViewModel = hiltViewModel()
) {
    val prestamo by viewModel.prestamo.collectAsState()
    val resumen by viewModel.resumen.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var tabSeleccionada by remember { mutableStateOf(0) }

    // Cargar préstamo cuando se abre la pantalla
    LaunchedEffect(prestamoId) {
        viewModel.cargarPrestamo(prestamoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle Préstamo #$prestamoId",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onNavigateToRegistrarPago,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Payment,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Registrar Pago")
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is DetallePrestamoViewModel.UIState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando información del préstamo...")
                }
            }
            is DetallePrestamoViewModel.UIState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = (uiState as DetallePrestamoViewModel.UIState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.cargarPrestamo(prestamoId) }) {
                        Text("Reintentar")
                    }
                }
            }
            else -> {
                if (prestamo != null && resumen != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Tabs
                        TabRow(selectedTabIndex = tabSeleccionada) {
                            Tab(
                                text = { Text("Resumen") },
                                selected = tabSeleccionada == 0,
                                onClick = { tabSeleccionada = 0 }
                            )
                            Tab(
                                text = { Text("Cuotas") },
                                selected = tabSeleccionada == 1,
                                onClick = { tabSeleccionada = 1 }
                            )
                            Tab(
                                text = { Text("Pagos") },
                                selected = tabSeleccionada == 2,
                                onClick = { tabSeleccionada = 2 }
                            )
                        }

                        // Contenido de la tab seleccionada
                        when (tabSeleccionada) {
                            0 -> ResumenTab(
                                prestamo = prestamo!!,
                                resumen = resumen!!,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            )
                            1 -> CuotasTab(
                                prestamoId = prestamoId,
                                modifier = Modifier.fillMaxSize()
                            )
                            2 -> PagosTab(
                                prestamoId = prestamoId,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Préstamo no encontrado")
                    }
                }
            }
        }
    }
}

@Composable
fun ResumenTab(
    prestamo: Prestamo,
    resumen: DetallePrestamoViewModel.ResumenPrestamo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Información Básica
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Información del Préstamo",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                InfoRow(
                    icon = Icons.Default.AttachMoney,
                    label = "Monto Principal",
                    value = "€${"%.2f".format(prestamo.montoPrincipal)}"
                )

                InfoRow(
                    icon = Icons.Default.AttachMoney,
                    label = "Tasa de Interés",
                    value = "${"%.1f".format(prestamo.tasaInteres)}% anual"
                )

                InfoRow(
                    icon = Icons.Default.Schedule,
                    label = "Cuotas",
                    value = "${prestamo.numeroCuotas} ${prestamo.obtenerFrecuenciaPagoTexto().lowercase()}"
                )

                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Tipo de Amortización",
                    value = prestamo.obtenerTipoAmortizacionTexto()
                )

                InfoRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Fecha Inicio",
                    value = prestamo.fechaInicio
                )

                InfoRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Primer Pago",
                    value = prestamo.fechaPrimerPago
                )
            }
        }

        // Estado y Progreso
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Estado y Progreso",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Estado:")
                    Text(
                        text = prestamo.obtenerEstadoTexto(),
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color(prestamo.obtenerColorEstado())
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Barra de progreso
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progreso:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = prestamo.calcularProgreso(),
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                    )
                    Text(
                        text = "${(prestamo.calcularProgreso() * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // Resumen Financiero
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Resumen Financiero",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total a Pagar:")
                    Text(
                        text = "€${"%.2f".format(prestamo.montoTotalPagar)}",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Intereses:")
                    Text(
                        text = "€${"%.2f".format(prestamo.totalIntereses)}",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Pagado:")
                    Text(
                        text = "€${"%.2f".format(resumen.totalPagado)}",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Saldo Pendiente:")
                    Text(
                        text = "€${"%.2f".format(resumen.saldoPendiente)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Estadísticas de Cuotas
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Estadísticas de Cuotas",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = "Pagadas",
                        value = resumen.cuotasPagadas.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatCard(
                        title = "Pendientes",
                        value = resumen.cuotasPendientes.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = "Vencidas",
                        value = resumen.cuotasVencidas.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatCard(
                        title = "Total",
                        value = prestamo.numeroCuotas.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CuotasTab(
    prestamoId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Funcionalidad de Cuotas en desarrollo...")
        Text(
            "Aquí se mostrará la tabla de amortización completa",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PagosTab(
    prestamoId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Historial de Pagos en desarrollo...")
        Text(
            "Aquí se mostrará el historial completo de pagos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetallePrestamoScreenPreview() {
    com.creditos.ui.theme.SistemaCreditosTheme {
        DetallePrestamoScreen(
            prestamoId = 1,
            onNavigateBack = {},
            onNavigateToRegistrarPago = {}
        )
    }
}