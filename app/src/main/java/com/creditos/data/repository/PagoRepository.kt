package com.creditos.data.repository

import com.creditos.data.dao.PagoDao
import com.creditos.data.entities.Pago
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PagoRepository @Inject constructor(
    private val pagoDao: PagoDao
) {

    suspend fun registrarPago(
        prestamoId: Int,
        cuotaId: Int?,
        monto: Double,
        metodoPago: String,
        referencia: String?,
        concepto: String,
        fechaPago: String
    ) {
        val pago = Pago(
            prestamo_id = prestamoId,
            cuota_id = cuotaId,
            monto = monto,
            fecha_pago = fechaPago,
            metodo_pago = metodoPago,
            referencia = referencia,
            concepto = concepto
        )
        pagoDao.insertar(pago)
    }

    fun obtenerPagosPorPrestamo(prestamoId: Int): Flow<List<Pago>> {
        return pagoDao.obtenerPorPrestamo(prestamoId)
    }

    suspend fun obtenerTotalPagado(prestamoId: Int): Double {
        return pagoDao.obtenerTotalPagado(prestamoId) ?: 0.0
    }

    suspend fun obtenerEstadisticasMetodosPago(): List<PagoDao.MetodoPagoEstadistica> {
        return pagoDao.obtenerEstadisticasMetodosPago()
    }
}