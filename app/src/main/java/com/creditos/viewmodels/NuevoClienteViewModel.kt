package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.entities.TipoDocumento
import com.creditos.data.repository.ClienteRepository
import com.creditos.data.repository.TipoDocumentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NuevoClienteViewModel @Inject constructor(
    private val clienteRepository: ClienteRepository,
    private val tipoDocumentoRepository: TipoDocumentoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _tiposDocumento = MutableStateFlow<List<TipoDocumento>>(emptyList())
    val tiposDocumento: StateFlow<List<TipoDocumento>> = _tiposDocumento.asStateFlow()

    init {
        cargarTiposDocumento()
    }

    private fun cargarTiposDocumento() {
        viewModelScope.launch {
            try {
                val tipos = tipoDocumentoRepository.obtenerTiposDocumentoActivos()
                _tiposDocumento.value = tipos
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error al cargar tipos de documento")
            }
        }
    }

    fun insertarCliente(
        nombre: String,
        apellido: String,
        tipoDocumentoId: Int,
        numeroDocumento: String,
        telefonoPrincipal: String,
        email: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                val fechaRegistro = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                clienteRepository.insertarCliente(
                    nombre = nombre,
                    apellido = apellido,
                    tipoDocumentoId = tipoDocumentoId,
                    numeroDocumento = numeroDocumento,
                    telefonoPrincipal = telefonoPrincipal,
                    email = email,
                    fechaRegistro = fechaRegistro
                )
                _uiState.value = UIState.Success
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error al guardar cliente: ${e.message}")
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