//NuevoClienteViewModel.kt
package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.entities.TipoDocumento
import com.creditos.data.repository.ClienteRepository
import com.creditos.data.repository.TipoDocumentoRepository
import com.creditos.data.repository.DireccionRepository
import com.creditos.data.entities.Direccion
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
    private val tipoDocumentoRepository: TipoDocumentoRepository,
    private val direccionRepository: DireccionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _tiposDocumento = MutableStateFlow<List<TipoDocumento>>(emptyList())
    val tiposDocumento: StateFlow<List<TipoDocumento>> = _tiposDocumento.asStateFlow()

    // Estado separado para la carga de tipos de documento
    private val _tiposDocumentoState = MutableStateFlow<TiposDocumentoState>(TiposDocumentoState.Loading)
    val tiposDocumentoState: StateFlow<TiposDocumentoState> = _tiposDocumentoState.asStateFlow()

    init {
        cargarTiposDocumento()
    }

    private fun cargarTiposDocumento() {
        viewModelScope.launch {
            _tiposDocumentoState.value = TiposDocumentoState.Loading
            try {
                android.util.Log.d("NuevoClienteVM", "Cargando tipos de documento...")
                val tipos = tipoDocumentoRepository.obtenerTiposDocumentoActivos()
                android.util.Log.d("NuevoClienteVM", "Tipos cargados: ${tipos.size}")

                _tiposDocumento.value = tipos
                if (tipos.isEmpty()) {
                    _tiposDocumentoState.value = TiposDocumentoState.Error("No se encontraron tipos de documento")
                    android.util.Log.w("NuevoClienteVM", "Lista de tipos de documento vacía")
                } else {
                    _tiposDocumentoState.value = TiposDocumentoState.Success
                }
            } catch (e: Exception) {
                val errorMsg = "Error al cargar tipos de documento: ${e.message}"
                _tiposDocumentoState.value = TiposDocumentoState.Error(errorMsg)
                android.util.Log.e("NuevoClienteVM", errorMsg, e)
            }
        }
    }

    fun insertarCliente(
        nombre: String,
        apellido: String,
        tipoDocumentoId: Int,
        numeroDocumento: String,
        telefonoPrincipal: String,
        email: String? = null,
        calle: String,
        numeroCalle: String,
        piso: String,
        puerta: String,
        codigoPostal: String,
        ciudad: String,
        provincia: String
    ) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                val fechaRegistro = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                // Primero insertar el cliente
                val clienteId = clienteRepository.insertarClienteYDevolverId(
                    nombre = nombre,
                    apellido = apellido,
                    tipoDocumentoId = tipoDocumentoId,
                    numeroDocumento = numeroDocumento,
                    telefonoPrincipal = telefonoPrincipal,
                    email = email,
                    fechaRegistro = fechaRegistro
                )

                // Luego insertar la dirección
                if (clienteId > 0) {
                    val direccion = Direccion(
                        clienteId = clienteId,
                        tipoDireccion = "PRINCIPAL",
                        calle = calle,
                        numero = if (numeroCalle.isNotBlank()) numeroCalle else null,
                        piso = if (piso.isNotBlank()) piso else null,
                        puerta = if (puerta.isNotBlank()) puerta else null,
                        codigoPostal = if (codigoPostal.isNotBlank()) codigoPostal else null,
                        ciudad = ciudad,
                        provincia = provincia,
                        predeterminada = true,
                        fechaCreacion = fechaRegistro
                    )

                    direccionRepository.insert(direccion)
                    _uiState.value = UIState.Success
                } else {
                    _uiState.value = UIState.Error("Error al obtener ID del cliente insertado")
                }

            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error al guardar cliente: ${e.message}")
                android.util.Log.e("NuevoClienteVM", "Error insertando cliente", e)
            }
        }
    }

    // Reiniciar el estado después de guardar exitosamente
    fun resetState() {
        _uiState.value = UIState.Idle
    }

    sealed class UIState {
        object Idle : UIState()
        object Loading : UIState()
        object Success : UIState()
        data class Error(val message: String) : UIState()
    }

    sealed class TiposDocumentoState {
        object Loading : TiposDocumentoState()
        object Success : TiposDocumentoState()
        data class Error(val message: String) : TiposDocumentoState()
    }
}