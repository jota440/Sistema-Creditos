package com.creditos.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.creditos.ui.dashboard.DashboardScreen
import com.creditos.ui.clientes.ListaClientesScreen
import androidx.compose.ui.tooling.preview.Preview
import com.creditos.ui.navigation.NavGraph
import com.creditos.ui.theme.SistemaCreditosTheme

@Composable
fun CreditosApp() {
    SistemaCreditosTheme {
        NavGraph()
    }
}

@Preview(showBackground = true)
@Composable
fun CreditosAppPreview() {
    CreditosApp()
}