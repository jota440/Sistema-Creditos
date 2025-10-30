package com.creditos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creditos.data.repository.PagoRepository
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
class RegistrarPagoViewModel @Inject constructor(
    private val pagoRepository: PagoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    fun registrarPago(
        monto: Double,
        metodoPago: String,
        referencia: String? = null,
        concepto: String = "CUOTA"
    ) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                // Por ahora usamos datos de prueba
                // En una implementación real, recibiríamos prestamoId y cuotaId como parámetros
                val fechaPago = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                pagoRepository.registrarPago(
                    prestamoId = 1, // Temporal - vendrá de la navegación
                    cuotaId = 1,    // Temporal - vendrá de la selección
                    monto = monto,
                    metodoPago = metodoPago,
                    referencia = referencia,
                    concepto = concepto,
                    fechaPago = fechaPago
                )

                _uiState.value = UIState.Success
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error al registrar pago: ${e.message}")
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

