//DetalleClienteScreen
@file:OptIn(ExperimentalMaterial3Api::class)
package com.creditos.ui.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creditos.data.entities.Cliente
import com.creditos.data.entities.Direccion
import com.creditos.data.entities.Prestamo
import com.creditos.viewmodels.DetalleClienteViewModel
import com.creditos.viewmodels.DireccionViewModel

@Composable
fun DetalleClienteScreen(
    clienteId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToNuevoPrestamo: () -> Unit,
    viewModel: DetalleClienteViewModel = hiltViewModel()
) {
    val cliente by viewModel.cliente.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var tabSeleccionada by remember { mutableStateOf(0) }

    // Cargar cliente cuando se abre la pantalla
    LaunchedEffect(clienteId) {
        viewModel.cargarCliente(clienteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = cliente?.let { "${it.nombre} ${it.apellido}" } ?: "Detalle Cliente"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onNavigateToNuevoPrestamo,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Nuevo Préstamo")
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is DetalleClienteViewModel.UIState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando información del cliente...")
                }
            }
            is DetalleClienteViewModel.UIState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = (uiState as DetalleClienteViewModel.UIState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.cargarCliente(clienteId) }) {
                        Text("Reintentar")
                    }
                }
            }
            else -> {
                if (cliente != null) {
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
                                text = { Text("Préstamos") },
                                selected = tabSeleccionada == 1,
                                onClick = { tabSeleccionada = 1 }
                            )
                            Tab(
                                text = { Text("Direcciones") },
                                selected = tabSeleccionada == 2,
                                onClick = { tabSeleccionada = 2 }
                            )
                        }

                        // Contenido de tabs
                        when (tabSeleccionada) {
                            0 -> ResumenTab(
                                cliente = cliente!!,
                                resumen = obtenerResumenEjemplo(),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            )
                            1 -> PrestamosTab(
                                clienteId = clienteId,
                                modifier = Modifier.fillMaxSize()
                            )
                            2 -> DireccionesTab(
                                clienteId = clienteId,
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
                        Text("Cliente no encontrado")
                    }
                }
            }
        }
    }
}

@Composable
fun ResumenTab(
    cliente: Cliente,
    resumen: DetalleClienteViewModel.ResumenPrestamo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta de Información Personal
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Información Personal",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Nombre Completo",
                    value = "${cliente.nombre} ${cliente.apellido}"
                )

                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Documento",
                    value = cliente.numeroDocumento
                )

                InfoRow(
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = cliente.telefonoPrincipal
                )

                cliente.telefonoSecundario?.let { telefono ->
                    InfoRow(
                        icon = Icons.Default.Phone,
                        label = "Teléfono Secundario",
                        value = telefono
                    )
                }

                cliente.email?.let { email ->
                    InfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = email
                    )
                }

                cliente.fechaNacimiento?.let { fechaNac ->
                    InfoRow(
                        icon = Icons.Default.Person,
                        label = "Fecha Nacimiento",
                        value = fechaNac
                    )
                }

                cliente.ocupacion?.let { ocupacion ->
                    InfoRow(
                        icon = Icons.Default.Person,
                        label = "Ocupación",
                        value = ocupacion
                    )
                }

                // Estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Estado:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (cliente.activo) "Activo" else "Inactivo",
                        color = if (cliente.activo) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Tarjeta de Información de Registro
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Información de Registro",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Fecha de Registro",
                    value = cliente.fechaRegistro
                )

                cliente.notas?.let { notas ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Notas:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = notas,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Resumen de Préstamos
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resumen de Préstamos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = "Préstamos Activos",
                        value = "0",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatCard(
                        title = "Total Adeudado",
                        value = "€${"%.2f".format(resumen.saldoPendiente)}",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = "Cuotas Pagadas",
                        value = resumen.cuotasPagadas.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatCard(
                        title = "Próximo Vencimiento",
                        value = "-",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PrestamosTab(
    clienteId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Préstamos del cliente en desarrollo...")
        Text(
            "Aquí se mostrarán los préstamos activos del cliente",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DireccionesTab(
    clienteId: Int,
    modifier: Modifier = Modifier,
    viewModel: DireccionViewModel = hiltViewModel()
) {
    val direcciones by viewModel.direcciones.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Cargar direcciones al abrir
    LaunchedEffect(clienteId) {
        viewModel.cargarDirecciones(clienteId)
    }

    Column(modifier = modifier.padding(16.dp)) {
        // Header con botón agregar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Direcciones",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = { /* Navegar a NuevaDireccionScreen */ },
                modifier = Modifier.height(36.dp)
            ) {
                Text("Agregar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is DireccionViewModel.UIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DireccionViewModel.UIState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error al cargar direcciones")
                }
            }
            else -> {
                if (direcciones.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No hay direcciones registradas")
                            Text(
                                "Presiona 'Agregar' para crear la primera",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(direcciones) { direccion ->
                            DireccionCard(
                                direccion = direccion,
                                onEditar = { /* Navegar a EditarDireccionScreen */ },
                                onEliminar = { viewModel.eliminarDireccion(direccion.id) },
                                onPredeterminar = {
                                    viewModel.marcarComoPredeterminada(direccion.id, clienteId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DireccionCard(
    direccion: Direccion,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onPredeterminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con tipo y predeterminada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = direccion.obtenerTipoDireccionTexto(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                if (direccion.predeterminada) {
                    Text(
                        text = "PREDETERMINADA",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dirección completa
            Text(direccion.obtenerDireccionCompleta())

            // Notas si existen
            direccion.notas?.let { notas ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notas: $notas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!direccion.predeterminada) {
                    TextButton(onClick = onPredeterminar) {
                        Text("Predeterminar")
                    }
                }
                TextButton(onClick = onEditar) {
                    Text("Editar")
                }
                TextButton(
                    onClick = onEliminar,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
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
            .padding(vertical = 4.dp),
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
    ElevatedCard(modifier = modifier) {
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

// Función temporal - eliminar cuando tengas el resumen real
private fun obtenerResumenEjemplo(): DetalleClienteViewModel.ResumenPrestamo {
    return DetalleClienteViewModel.ResumenPrestamo(
        prestamo = Prestamo.crearPrestamoEjemplo(),
        totalPagado = 250.0,
        saldoPendiente = 750.0,
        cuotasPagadas = 1,
        cuotasPendientes = 11,
        cuotasVencidas = 0,
        proximaCuota = null
    )
}

@Composable
@Preview(showBackground = true)
fun DetalleClienteContentPreview() {
    com.creditos.ui.theme.SistemaCreditosTheme {
        DetalleClienteScreen(
            clienteId = 1,
            onNavigateBack = {},
            onNavigateToNuevoPrestamo = {}
        )
    }
}