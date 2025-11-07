package com.creditos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.ColumnInfo
import com.creditos.data.entities.Pago
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoDao {

    @Insert
    suspend fun insertar(pago: Pago)

    @Query("SELECT * FROM pr_pagos WHERE prestamo_id = :prestamoId ORDER BY fecha_pago DESC")
    fun obtenerPorPrestamo(prestamoId: Int): Flow<List<Pago>>

    @Query("SELECT * FROM pr_pagos WHERE cuota_id = :cuotaId ORDER BY fecha_pago DESC")
    suspend fun obtenerPorCuota(cuotaId: Int): List<Pago>

    @Query("SELECT SUM(monto) FROM pr_pagos WHERE prestamo_id = :prestamoId")
    suspend fun obtenerTotalPagado(prestamoId: Int): Double?

    @Query("SELECT COUNT(*) FROM pr_pagos WHERE prestamo_id = :prestamoId")
    suspend fun obtenerCantidadPagos(prestamoId: Int): Int

    @Query("SELECT * FROM pr_pagos WHERE fecha_pago BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha_pago DESC")
    suspend fun obtenerPagosPorRangoFechas(fechaInicio: String, fechaFin: String): List<Pago>

    @Query("SELECT metodo_pago, COUNT(*) as cantidad FROM pr_pagos GROUP BY metodo_pago")
    suspend fun obtenerEstadisticasMetodosPago(): List<MetodoPagoEstadistica>

    data class MetodoPagoEstadistica(
        @ColumnInfo(name = "metodo_pago")
        val metodoPago: String,
        val cantidad: Int
    )
}