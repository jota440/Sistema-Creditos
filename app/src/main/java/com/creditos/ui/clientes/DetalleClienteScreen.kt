@file:OptIn(ExperimentalMaterial3Api::class)
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creditos.data.entities.Cliente
import com.creditos.viewmodels.DetalleClienteViewModel

@Composable
fun DetalleClienteScreen(
    clienteId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToNuevoPrestamo: () -> Unit,
    viewModel: DetalleClienteViewModel = hiltViewModel()
) {
    val cliente by viewModel.cliente.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

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
                    DetalleClienteContent(
                        cliente = cliente!!,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
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
fun DetalleClienteContent(
    cliente: Cliente,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
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

        // Resumen de Préstamos (placeholder por ahora)
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
                        value = "€0.00",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = "Cuotas Pendientes",
                        value = "0",
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

@Composable
@Preview(showBackground = true)
fun DetalleClienteContentPreview() {
    com.creditos.ui.theme.SistemaCreditosTheme {
        DetalleClienteContent(
            cliente = Cliente(
                id = 1,
                nombre = "Juan",
                apellido = "Pérez",
                tipoDocumentoId = 1,
                numeroDocumento = "12345678",
                telefonoPrincipal = "+34 600 000 000",
                email = "juan@email.com",
                fechaNacimiento = "1990-01-01",
                ocupacion = "Desarrollador",
                notas = "Cliente confiable, siempre paga a tiempo.",
                fechaRegistro = "2024-01-15",
                activo = true
            )
        )
    }
}