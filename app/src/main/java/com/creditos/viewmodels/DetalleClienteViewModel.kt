package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.entities.Cliente
import com.creditos.data.entities.Prestamo
import com.creditos.data.repository.ClienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleClienteViewModel @Inject constructor(
    private val clienteRepository: ClienteRepository
) : ViewModel() {

    private val _cliente = MutableStateFlow<Cliente?>(null)
    val cliente: StateFlow<Cliente?> = _cliente.asStateFlow()

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    fun cargarCliente(clienteId: Int) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                val clienteEncontrado = clienteRepository.obtenerClientePorId(clienteId)
                _cliente.value = clienteEncontrado
                _uiState.value = if (clienteEncontrado != null) UIState.Success else UIState.Error("Cliente no encontrado")
            } catch (e: Exception) {
                _uiState.value = UIState.Error(e.message ?: "Error al cargar cliente")
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
        val proximaCuota: String?
    )
    sealed class UIState {
        object Idle : UIState()
        object Loading : UIState()
        object Success : UIState()
        data class Error(val message: String) : UIState()
    }
}