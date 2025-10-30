package com.creditos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.creditos.data.entities.Cuota
import kotlinx.coroutines.flow.Flow

@Dao
interface CuotaDao {

    @Insert
    suspend fun insertar(cuota: Cuota)

    @Insert
    suspend fun insertarTodos(cuotas: List<Cuota>)

    @Update
    suspend fun actualizar(cuota: Cuota)

    @Query("SELECT * FROM pr_cuotas WHERE prestamo_id = :prestamoId ORDER BY numero_cuota")
    suspend fun obtenerPorPrestamo(prestamoId: Int): List<Cuota>

    @Query("SELECT * FROM pr_cuotas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Cuota?

    @Query("SELECT * FROM pr_cuotas WHERE prestamo_id = :prestamoId AND estado = 'PENDIENTE' ORDER BY fecha_vencimiento LIMIT 1")
    suspend fun obtenerProximaCuotaPendiente(prestamoId: Int): Cuota?

    @Query("SELECT * FROM pr_cuotas WHERE estado IN ('PENDIENTE', 'VENCIDA') AND fecha_vencimiento <= date('now') ORDER BY fecha_vencimiento")
    suspend fun obtenerCuotasVencidas(): List<Cuota>

    @Query("UPDATE pr_cuotas SET estado = 'VENCIDA', dias_mora = CAST((julianday('now') - julianday(fecha_vencimiento)) AS INTEGER) WHERE estado IN ('PENDIENTE', 'PARCIAL') AND fecha_vencimiento < date('now')")
    suspend fun actualizarCuotasVencidas()

    @Query("SELECT COUNT(*) FROM pr_cuotas WHERE prestamo_id = :prestamoId AND estado = 'PAGADA'")
    suspend fun obtenerCuotasPagadasCount(prestamoId: Int): Int

    @Query("SELECT * FROM pr_cuotas WHERE prestamo_id = :prestamoId AND estado IN ('PENDIENTE', 'VENCIDA') ORDER BY fecha_vencimiento")
    suspend fun obtenerCuotasPendientes(prestamoId: Int): List<Cuota>
}