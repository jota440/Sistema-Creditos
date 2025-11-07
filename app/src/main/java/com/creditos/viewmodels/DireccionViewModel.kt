//DireccionViewModel.kt
package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.entities.Direccion
import com.creditos.data.repository.DireccionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DireccionViewModel @Inject constructor(
    private val direccionRepository: DireccionRepository
) : ViewModel() {

    private val _direcciones = MutableStateFlow<List<Direccion>>(emptyList())
    val direcciones: StateFlow<List<Direccion>> = _direcciones.asStateFlow()

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    fun cargarDirecciones(clienteId: Int) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                val direccionesList = direccionRepository.getByClienteId(clienteId)
                _direcciones.value = direccionesList
                _uiState.value = UIState.Success
            } catch (e: Exception) {
                _uiState.value = UIState.Error(e.message ?: "Error al cargar direcciones")
            }
        }
    }

    fun eliminarDireccion(direccionId: Int) {
        viewModelScope.launch {
            try {
                direccionRepository.deleteById(direccionId)
                // Recargar la lista
                val currentCliente = _direcciones.value.firstOrNull()?.clienteId
                currentCliente?.let { cargarDirecciones(it) }
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error al eliminar dirección")
            }
        }
    }

    fun marcarComoPredeterminada(direccionId: Int, clienteId: Int) {
        viewModelScope.launch {
            try {
                direccionRepository.marcarComoPredeterminada(direccionId, clienteId)
                cargarDirecciones(clienteId)
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error al marcar como predeterminada")
            }
        }
    }

    sealed class UIState {
        object Idle : UIState()
        object Loading : UIState()
        object Success : UIState()
        data class Error(val message: String) : UIState()
    }
}