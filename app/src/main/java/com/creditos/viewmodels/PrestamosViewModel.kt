package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.dao.PrestamoConClienteInfo
import com.creditos.data.dao.PrestamoDao
import com.creditos.data.repository.PrestamoRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrestamosViewModel @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    private val _prestamos = MutableStateFlow<List<PrestamoConClienteInfo>>(emptyList())
    val prestamos: StateFlow<List<PrestamoConClienteInfo>> = _prestamos.asStateFlow()

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        cargarPrestamosActivos()
    }

    fun cargarPrestamosActivos() {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                val prestamosList = prestamoRepository.obtenerPrestamosActivos()
                _prestamos.value = prestamosList
                _uiState.value = UIState.Success
            } catch (e: Exception) {
                _uiState.value = UIState.Error(e.message ?: "Error al cargar préstamos")
            }
        }
    }

    fun buscarPrestamos(query: String) {
        viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    cargarPrestamosActivos()
                } else {
                    // Implementar búsqueda por nombre de cliente
                    val resultados = _prestamos.value.filter {
                        it.nombreCliente.contains(query, ignoreCase = true)
                    }
                    _prestamos.value = resultados
                }
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error en la búsqueda")
            }
        }
    }

    fun cargarPrestamosPorEstado(estado: String) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                // Esta función necesita ser implementada en el repository
                // Por ahora filtramos de la lista actual
                val prestamosFiltrados = if (estado == "TODOS") {
                    prestamoRepository.obtenerPrestamosActivos()
                } else {
                    _prestamos.value.filter { it.prestamo.estado == estado }
                }
                _prestamos.value = prestamosFiltrados
                _uiState.value = UIState.Success
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error al filtrar préstamos")
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
