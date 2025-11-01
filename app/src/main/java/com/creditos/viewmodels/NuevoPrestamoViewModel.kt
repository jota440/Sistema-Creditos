package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.creditos.data.entities.Cliente
import com.creditos.data.repository.ClienteRepository
import com.creditos.data.repository.PrestamoRepository

@HiltViewModel
class NuevoPrestamoViewModel @Inject constructor(
    private val prestamoRepository: PrestamoRepository,
    private val clienteRepository: ClienteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> = _clientes.asStateFlow()

    init {
        cargarClientesActivos()
    }

    private fun cargarClientesActivos() {
        viewModelScope.launch {
            try {
                val clientesList = clienteRepository.obtenerClientesActivos()
                _clientes.value = clientesList
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error al cargar clientes")
            }
        }
    }

    fun crearPrestamo(
        clienteId: Int,
        montoPrincipal: Double,
        tasaInteres: Double,
        numeroCuotas: Int,
        frecuenciaPago: String,
        tipoAmortizacion: String,
        fechaInicio: String,
        notas: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                prestamoRepository.crearPrestamoConCuotas(
                    clienteId = clienteId,
                    montoPrincipal = montoPrincipal,
                    tasaInteres = tasaInteres,
                    numeroCuotas = numeroCuotas,
                    frecuenciaPago = frecuenciaPago,
                    tipoAmortizacion = tipoAmortizacion,
                    fechaInicio = fechaInicio,
                    notas = notas
                )
                _uiState.value = UIState.Success
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error al crear préstamo: ${e.message}")
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