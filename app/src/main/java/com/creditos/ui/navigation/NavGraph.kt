//NavGraph.kt
package com.creditos.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.creditos.BuildConfig
import com.creditos.ui.clientes.ClientesScreen
import com.creditos.ui.clientes.DetalleClienteScreen
import com.creditos.ui.clientes.NuevoClienteScreen
import com.creditos.ui.dashboard.DashboardScreen
import com.creditos.ui.dev.DevSettingsScreen
import com.creditos.ui.pagos.RegistrarPagoScreen
import com.creditos.ui.prestamos.DetallePrestamoScreen
import com.creditos.ui.prestamos.NuevoPrestamoScreen
import com.creditos.ui.prestamos.PrestamosScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Observar ruta actual para resaltar el item seleccionado
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ✅ Drawer (Menú Lateral)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                DrawerContent(
                    navController = navController,
                    currentRoute = currentRoute,
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        // ✅ NavHost (Navegación)
        NavHost(
            navController = navController,
            startDestination = Screens.Dashboard.route
        ) {
            // ========== DASHBOARD ==========
            composable(Screens.Dashboard.route) {
                DashboardScreen(
                    onNavigateToClientes = { navController.navigate(Screens.Clientes.route) },
                    onNavigateToPrestamos = { navController.navigate(Screens.Prestamos.route) },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            // ========== CLIENTES ==========
            composable(Screens.Clientes.route) {
                ClientesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNuevoCliente = { navController.navigate(Screens.NuevoCliente.route) },
                    onNavigateToDetalleCliente = { clienteId ->
                        navController.navigate("${Screens.DetalleCliente.route}/$clienteId")
                    }
                )
            }

            composable(Screens.NuevoCliente.route) {
                NuevoClienteScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("${Screens.DetalleCliente.route}/{clienteId}") { backStackEntry ->
                val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull() ?: 0
                DetalleClienteScreen(
                    clienteId = clienteId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNuevoPrestamo = { navController.navigate(Screens.NuevoPrestamo.route) }
                )
            }

            // ========== PRÉSTAMOS ==========
            composable(Screens.Prestamos.route) {
                PrestamosScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNuevoPrestamo = { navController.navigate(Screens.NuevoPrestamo.route) },
                    onNavigateToDetallePrestamo = { prestamoId ->
                        navController.navigate("${Screens.DetallePrestamo.route}/$prestamoId")
                    }
                )
            }

            composable(Screens.NuevoPrestamo.route) {
                NuevoPrestamoScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("${Screens.DetallePrestamo.route}/{prestamoId}") { backStackEntry ->
                val prestamoId = backStackEntry.arguments?.getString("prestamoId")?.toIntOrNull() ?: 0
                DetallePrestamoScreen(
                    prestamoId = prestamoId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRegistrarPago = {
                        navController.navigate(Screens.RegistrarPago.route)
                    }
                )
            }

            // ========== PAGOS ==========
            composable(Screens.RegistrarPago.route) {
                RegistrarPagoScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ========== DEV SETTINGS (Solo DEBUG) ==========
            if (BuildConfig.DEBUG) {
                composable(Screens.DevSettings.route) {
                    DevSettingsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

/**
 * ✅ Contenido del Drawer (Menú Lateral)
 */
@Composable
private fun DrawerContent(
    navController: NavHostController,
    currentRoute: String?,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        // ========== HEADER ==========
        DrawerHeader()

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()

        // ========== MENÚ PRINCIPAL ==========
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            // ← En español
            label = { Text("Inicio") },
            selected = currentRoute == Screens.Dashboard.route,
            onClick = {
                navController.navigate(Screens.Dashboard.route) {
                    popUpTo(Screens.Dashboard.route) { inclusive = true }
                }
                onCloseDrawer()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Clientes") },
            selected = currentRoute == Screens.Clientes.route,
            onClick = {
                navController.navigate(Screens.Clientes.route)
                onCloseDrawer()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
            label = { Text("Préstamos") },
            selected = currentRoute == Screens.Prestamos.route,
            onClick = {
                navController.navigate(Screens.Prestamos.route)
                onCloseDrawer()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // ========== DEV SETTINGS (Solo DEBUG) ==========
        if (BuildConfig.DEBUG) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))


            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                label = {
                    Row {
                        Text("Ajustes DB")        // ← Nuevo texto
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🔧", style = MaterialTheme.typography.bodySmall)
                    }
                },
                selected = currentRoute == Screens.DevSettings.route,
                onClick = {
                    navController.navigate(Screens.DevSettings.route)
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            )
        }

        // ========== VERSIÓN (al final) ==========
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "v${BuildConfig.VERSION_NAME} ${if (BuildConfig.DEBUG) "DEBUG" else ""}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * ✅ Header del Drawer
 */
@Composable
private fun DrawerHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sistema Créditos",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * ✅ Definición de Rutas
 */
sealed class Screens(val route: String) {
    object Dashboard : Screens("dashboard")
    object Clientes : Screens("clientes")
    object NuevoCliente : Screens("nuevo_cliente")
    object DetalleCliente : Screens("detalle_cliente")
    object Prestamos : Screens("prestamos")
    object NuevoPrestamo : Screens("nuevo_prestamo")
    object DetallePrestamo : Screens("detalle_prestamo")
    object RegistrarPago : Screens("registrar_pago")
    object DevSettings : Screens("dev_settings") // ✅ Nueva ruta
}