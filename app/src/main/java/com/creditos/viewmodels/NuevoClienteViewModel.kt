//NuevoClienteViewModel.kt
package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.entities.*
import com.creditos.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.creditos.utils.DateUtils

@HiltViewModel
class NuevoClienteViewModel @Inject constructor(
    private val clienteRepository: ClienteRepository,
    private val tipoDocumentoRepository: TipoDocumentoRepository,
    private val direccionRepository: DireccionRepository,
    private val paisRepository: PaisRepository,
    private val provinciaRepository: ProvinciaRepository,
    private val codigoPostalRepository: CodigoPostalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _tiposDocumento = MutableStateFlow<List<TipoDocumento>>(emptyList())
    val tiposDocumento: StateFlow<List<TipoDocumento>> = _tiposDocumento.asStateFlow()

    private val _tiposDocumentoState = MutableStateFlow<TiposDocumentoState>(TiposDocumentoState.Loading)
    val tiposDocumentoState: StateFlow<TiposDocumentoState> = _tiposDocumentoState.asStateFlow()

    // Datos de ubicación
    private val _paises = MutableStateFlow<List<Pais>>(emptyList())
    val paises: StateFlow<List<Pais>> = _paises.asStateFlow()

    private val _provincias = MutableStateFlow<List<Provincia>>(emptyList())
    val provincias: StateFlow<List<Provincia>> = _provincias.asStateFlow()

    private val _codigosPostales = MutableStateFlow<List<CodigoPostal>>(emptyList())
    val codigosPostales: StateFlow<List<CodigoPostal>> = _codigosPostales.asStateFlow()

    init {
        cargarTiposDocumento()
        cargarPaises()
        cargarProvincias()
        cargarTodosCodigosPostales()
    }

    private fun cargarTiposDocumento() {
        viewModelScope.launch {
            _tiposDocumentoState.value = TiposDocumentoState.Loading
            try {
                val tipos = tipoDocumentoRepository.obtenerTiposDocumentoActivos()
                _tiposDocumento.value = tipos
                _tiposDocumentoState.value = if (tipos.isEmpty()) {
                    TiposDocumentoState.Error("No se encontraron tipos de documento")
                } else {
                    TiposDocumentoState.Success
                }
            } catch (e: Exception) {
                android.util.Log.e("NuevoClienteVM", "Error cargando tipos documento", e)
                _tiposDocumentoState.value = TiposDocumentoState.Error("Error: ${e.message}")
            }
        }
    }

    private fun cargarPaises() {
        viewModelScope.launch {
            try {
                paisRepository.getAllPaises().collect { listaPaises ->
                    _paises.value = listaPaises
                    android.util.Log.d("NuevoClienteVM", "Países cargados: ${listaPaises.size}")
                }
            } catch (e: Exception) {
                android.util.Log.e("NuevoClienteVM", "Error cargando países", e)
            }
        }
    }

    private fun cargarProvincias() {
        viewModelScope.launch {
            try {
                provinciaRepository.getAllProvincias().collect { listaProvincias ->
                    _provincias.value = listaProvincias
                    android.util.Log.d("NuevoClienteVM", "Provincias cargadas: ${listaProvincias.size}")
                }
            } catch (e: Exception) {
                android.util.Log.e("NuevoClienteVM", "Error cargando provincias", e)
            }
        }
    }

    private fun cargarTodosCodigosPostales() {
        viewModelScope.launch {
            try {
                // Cargar todos los códigos postales disponibles
                codigoPostalRepository.getByPrefijo("").collect { listaCPs ->
                    _codigosPostales.value = listaCPs
                    android.util.Log.d("NuevoClienteVM", "Códigos postales cargados: ${listaCPs.size}")
                }
            } catch (e: Exception) {
                android.util.Log.e("NuevoClienteVM", "Error cargando códigos postales", e)
            }
        }
    }

    /**
     * Buscar códigos postales por prefijo de provincia (primeros 2 dígitos)
     * Ejemplo: Barcelona = "08" -> Filtra 08001, 08002, etc.
     */
    fun buscarCodigosPostalesPorProvincia(codigoProvincia: String) {
        if (codigoProvincia.length != 2) {
            // Si no es válido, cargar todos
            cargarTodosCodigosPostales()
            return
        }

        viewModelScope.launch {
            try {
                codigoPostalRepository.getByPrefijo(codigoProvincia).collect { lista ->
                    _codigosPostales.value = lista
                    android.util.Log.d("NuevoClienteVM",
                        "CPs filtrados por provincia $codigoProvincia: ${lista.size}")
                }
            } catch (e: Exception) {
                android.util.Log.e("NuevoClienteVM", "Error buscando CPs por provincia", e)
                _codigosPostales.value = emptyList()
            }
        }
    }

    /**
     * Buscar códigos postales por ciudad
     */
    fun buscarCodigosPostalesPorCiudad(ciudad: String) {
        if (ciudad.isBlank()) {
            cargarTodosCodigosPostales()
            return
        }

        viewModelScope.launch {
            try {
                codigoPostalRepository.searchByCiudad(ciudad).collect { lista ->
                    _codigosPostales.value = lista
                    android.util.Log.d("NuevoClienteVM",
                        "CPs filtrados por ciudad '$ciudad': ${lista.size}")
                }
            } catch (e: Exception) {
                android.util.Log.e("NuevoClienteVM", "Error buscando CPs por ciudad", e)
                _codigosPostales.value = emptyList()
            }
        }
    }

    /**
     * Validar DNI español (8 dígitos + letra)
     * Algoritmo oficial: número % 23 = posición en "TRWAGMYFPDXBNJZSQVHLCKE"
     */
    fun validarDNI(dni: String): Boolean {
        if (dni.length != 9) return false

        val numero = dni.substring(0, 8).toIntOrNull() ?: return false
        val letra = dni.substring(8, 9).uppercase()

        val letras = "TRWAGMYFPDXBNJZSQVHLCKE"
        val letraEsperada = letras[numero % 23].toString()

        return letra == letraEsperada
    }

    /**
     * Validar NIE (Número de Identidad de Extranjero)
     * Formato: X/Y/Z + 7 dígitos + letra de control
     */
    fun validarNIE(nie: String): Boolean {
        if (nie.length != 9) return false

        val primeraLetra = nie.substring(0, 1).uppercase()
        if (primeraLetra !in listOf("X", "Y", "Z")) return false

        // Reemplazar primera letra por número
        val numeroInicial = when (primeraLetra) {
            "X" -> "0"
            "Y" -> "1"
            "Z" -> "2"
            else -> return false
        }

        val numero = (numeroInicial + nie.substring(1, 8)).toIntOrNull() ?: return false
        val letra = nie.substring(8, 9).uppercase()

        val letras = "TRWAGMYFPDXBNJZSQVHLCKE"
        val letraEsperada = letras[numero % 23].toString()

        return letra == letraEsperada
    }

    /**
     * Validar CIF (Código de Identificación Fiscal)
     * Formato: Letra + 7 dígitos + dígito/letra de control
     */
    fun validarCIF(cif: String): Boolean {
        if (cif.length != 9) return false

        val primeraLetra = cif.substring(0, 1).uppercase()
        // Letras válidas para CIF
        if (primeraLetra !in "ABCDEFGHJNPQRSUVW") return false

        val numeros = cif.substring(1, 8)
        if (!numeros.all { it.isDigit() }) return false

        val ultimoCaracter = cif.substring(8, 9)

        // Calcular dígito de control
        var sumaPar = 0
        var sumaImpar = 0

        for (i in numeros.indices) {
            val digito = numeros[i].toString().toInt()
            if (i % 2 == 0) {
                // Posición impar (0, 2, 4, 6) - se multiplica por 2
                val doble = digito * 2
                sumaImpar += if (doble > 9) doble - 9 else doble
            } else {
                // Posición par (1, 3, 5) - se suma directamente
                sumaPar += digito
            }
        }

        val sumaTotal = sumaPar + sumaImpar
        val unidadSiguiente = ((sumaTotal / 10) + 1) * 10
        val digitoControl = unidadSiguiente - sumaTotal

        // Algunos CIFs usan letra en lugar de dígito
        val letraControl = "JABCDEFGHI"[digitoControl].toString()

        return ultimoCaracter == digitoControl.toString() ||
                ultimoCaracter.uppercase() == letraControl
    }

    /**
     * Obtener el tipo de documento por su código
     */
    fun obtenerTipoDocumentoPorCodigo(codigo: String): TipoDocumento? {
        return _tiposDocumento.value.find { it.codigo == codigo }
    }

    /**
     * Verificar si un tipo de documento requiere validación
     */
    fun requiereValidacion(tipoDocumento: TipoDocumento?): Boolean {
        return tipoDocumento?.requiereValidacion == 1
    }

    /**
     * Insertar nuevo cliente con dirección
     */
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
        provincia: String,
        pais: String = "España"
    ) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                //val fechaRegistro = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(Date())
                val fechaRegistro = Date()
                val fechaCreacionStr: String? = DateUtils.formatDateTime(fechaRegistro)

                // Insertar cliente y obtener ID
                val clienteId = clienteRepository.insertarClienteYDevolverId(
                    nombre = nombre.trim(),
                    apellido = apellido.trim(),
                    tipoDocumentoId = tipoDocumentoId,
                    numeroDocumento = numeroDocumento.trim().uppercase(),
                    telefonoPrincipal = telefonoPrincipal.trim(),
                    email = email?.trim()?.takeIf { it.isNotBlank() },
                    fechaRegistro = fechaRegistro
                )

                if (clienteId > 0) {
                    // Insertar dirección principal
                    val direccion = Direccion(
                        clienteId = clienteId,
                        tipoDireccion = "PRINCIPAL",
                        calle = calle.trim(),
                        numero = numeroCalle.trim().takeIf { it.isNotBlank() },
                        piso = piso.trim().takeIf { it.isNotBlank() },
                        puerta = puerta.trim().takeIf { it.isNotBlank() },
                        codigoPostal = codigoPostal.trim().takeIf { it.isNotBlank() },
                        ciudad = ciudad.trim(),
                        provincia = provincia.trim(),
                        pais = pais.trim(),
                        predeterminada = true,
                        fechaCreacion = fechaCreacionStr ?: "" // o algún valor por defecto si es null
                    )

                    direccionRepository.insert(direccion)

                    android.util.Log.d("NuevoClienteVM",
                        "Cliente y dirección insertados correctamente. ID: $clienteId")

                    _uiState.value = UIState.Success
                } else {
                    android.util.Log.e("NuevoClienteVM", "Error: ID de cliente inválido")
                    _uiState.value = UIState.Error("Error al obtener ID del cliente")
                }
            } catch (e: Exception) {
                android.util.Log.e("NuevoClienteVM", "Error insertando cliente", e)
                _uiState.value = UIState.Error("Error al guardar: ${e.message}")
            }
        }
    }

    /**
     * Resetear el estado del formulario
     */
    fun resetState() {
        _uiState.value = UIState.Idle
    }

    /**
     * Estados de la UI
     */
    sealed class UIState {
        object Idle : UIState()
        object Loading : UIState()
        object Success : UIState()
        data class Error(val message: String) : UIState()
    }

    /**
     * Estados de carga de tipos de documento
     */
    sealed class TiposDocumentoState {
        object Loading : TiposDocumentoState()
        object Success : TiposDocumentoState()
        data class Error(val message: String) : TiposDocumentoState()
    }
}