package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.entities.Prestamo
import com.creditos.data.repository.PrestamoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetallePrestamoViewModel @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    private val _prestamo = MutableStateFlow<Prestamo?>(null)
    val prestamo: StateFlow<Prestamo?> = _prestamo.asStateFlow()

    private val _resumen = MutableStateFlow<ResumenPrestamo?>(null)
    val resumen: StateFlow<ResumenPrestamo?> = _resumen.asStateFlow()

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    fun cargarPrestamo(prestamoId: Int) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                val prestamoEncontrado = prestamoRepository.obtenerPrestamoPorId(prestamoId)
                _prestamo.value = prestamoEncontrado

                if (prestamoEncontrado != null) {
                    val resumenPrestamo = prestamoRepository.obtenerResumenPrestamo(prestamoId)
                    _resumen.value = ResumenPrestamo(
                        prestamo = prestamoEncontrado,
                        totalPagado = resumenPrestamo.totalPagado,
                        saldoPendiente = resumenPrestamo.saldoPendiente,
                        cuotasPagadas = resumenPrestamo.cuotasPagadas,
                        cuotasPendientes = resumenPrestamo.cuotasPendientes,
                        cuotasVencidas = resumenPrestamo.cuotasVencidas,
                        proximaCuota = resumenPrestamo.proximaCuota
                    )
                    _uiState.value = UIState.Success
                } else {
                    _uiState.value = UIState.Error("Préstamo no encontrado")
                }
            } catch (e: Exception) {
                _uiState.value = UIState.Error(e.message ?: "Error al cargar préstamo")
            }
        }
    }

    data class ResumenPrestamo(
        val prestamo: Prestamo,
        val totalPagado: Double,
        val saldoPendiente: Double,
        val cuotasPagadas: Int,
        val cuotasPendientes: Int,
        val cuotasVencidas: Int,
        val proximaCuota: com.creditos.data.entities.Cuota?
    )

    sealed class UIState {
        object Idle : UIState()
        object Loading : UIState()
        object Success : UIState()
        data class Error(val message: String) : UIState()
    }
}
