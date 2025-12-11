package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.entities.Cliente
import com.creditos.data.repository.ClienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientesViewModel @Inject constructor(
    private val clienteRepository: ClienteRepository
) : ViewModel() {

    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> = _clientes.asStateFlow()

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        cargarClientes()
    }

    fun cargarClientes() {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                val clientesList = clienteRepository.obtenerTodosClientes()
                _clientes.value = clientesList
                _uiState.value = UIState.Success
            } catch (e: Exception) {
                _uiState.value = UIState.Error(e.message ?: "Error al cargar clientes")
            }
        }
    }

    fun buscarClientes(query: String) {
        viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    cargarClientes()
                } else {
                    val resultados = clienteRepository.buscarClientes(query)
                    _clientes.value = resultados
                }
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error en la búsqueda")
            }
        }
    }


    fun buscarPorApellidoPattern(pattern: String) {
        viewModelScope.launch {
            try {
                if (pattern.isBlank()) {
                    cargarClientes()
                } else {
                    val resultados = clienteRepository.buscarClientesPorApellidoPattern(pattern)
                    _clientes.value = resultados
                    _uiState.value = UIState.Success
                }
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error en la búsqueda por apellido")
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