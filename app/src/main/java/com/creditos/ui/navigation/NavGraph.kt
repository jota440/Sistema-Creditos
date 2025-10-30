package com.creditos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.creditos.ui.clientes.ClientesScreen
import com.creditos.ui.clientes.DetalleClienteScreen
import com.creditos.ui.clientes.NuevoClienteScreen
import com.creditos.ui.dashboard.DashboardScreen
import com.creditos.ui.pagos.RegistrarPagoScreen
import com.creditos.ui.prestamos.DetallePrestamoScreen
import com.creditos.ui.prestamos.NuevoPrestamoScreen
import com.creditos.ui.prestamos.PrestamosScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Dashboard.route
    ) {
        composable(Screens.Dashboard.route) {
            DashboardScreen(
                onNavigateToClientes = { navController.navigate(Screens.Clientes.route) },
                onNavigateToPrestamos = { navController.navigate(Screens.Prestamos.route) }
            )
        }

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

        composable(Screens.RegistrarPago.route) {
            RegistrarPagoScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screens(val route: String) {
    object Dashboard : Screens("dashboard")
    object Clientes : Screens("clientes")
    object NuevoCliente : Screens("nuevo_cliente")
    object DetalleCliente : Screens("detalle_cliente")
    object Prestamos : Screens("prestamos")
    object NuevoPrestamo : Screens("nuevo_prestamo")
    object DetallePrestamo : Screens("detalle_prestamo")
    object RegistrarPago : Screens("registrar_pago")
}

