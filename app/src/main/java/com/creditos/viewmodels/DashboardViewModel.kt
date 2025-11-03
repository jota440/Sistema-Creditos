//DashboardViewModel.kt
package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.repository.ClienteRepository
import com.creditos.data.repository.PrestamoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val prestamoRepository: PrestamoRepository,
    private val clienteRepository: ClienteRepository
) : ViewModel() {

    private val _prestamosActivos = MutableStateFlow(0)
    val prestamosActivos: StateFlow<Int> = _prestamosActivos

    private val _totalClientes = MutableStateFlow(0)
    val totalClientes: StateFlow<Int> = _totalClientes

    init {
        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        viewModelScope.launch {
            // 🔍 AQUÍ puedes poner breakpoints para hacer debug

            // Obtener préstamos activos
            val prestamos = prestamoRepository.obtenerPrestamosPorEstado("ACTIVO")
            _prestamosActivos.value = prestamos.size

            // 📝 Log para ver en Logcat
            android.util.Log.d("DEBUG_DASHBOARD", "Préstamos activos: ${prestamos.size}")
            android.util.Log.d("DEBUG_DASHBOARD", "Lista de préstamos: $prestamos")

            // Obtener total de clientes
            val clientes = clienteRepository.obtenerClientesActivos()
            _totalClientes.value = clientes.size

            // 📝 Log para ver en Logcat
            android.util.Log.d("DEBUG_DASHBOARD", "Total clientes: ${clientes.size}")
            android.util.Log.d("DEBUG_DASHBOARD", "Lista de clientes: $clientes")
        }
    }

    fun actualizarEstadisticas() {
        cargarEstadisticas()
    }
}