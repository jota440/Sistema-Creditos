//PrestamoDao.kt
package com.creditos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Embedded
import com.creditos.data.entities.Prestamo

@Dao
interface PrestamoDao {

    @Insert
    suspend fun insertar(prestamo: Prestamo): Long

    @Update
    suspend fun actualizar(prestamo: Prestamo)

    @Query("SELECT * FROM pr_prestamos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Prestamo?

    @Query("SELECT * FROM pr_prestamos WHERE cliente_id = :clienteId ORDER BY fecha_creacion DESC")
    suspend fun obtenerPorCliente(clienteId: Int): List<Prestamo>

    @Query("SELECT * FROM pr_prestamos WHERE estado = :estado ORDER BY fecha_creacion DESC")
    suspend fun obtenerPorEstado(estado: String): List<Prestamo>

    @Query("""
        SELECT p.*, (c.nombre || ' ' || c.apellido) AS nombreCliente
        FROM pr_prestamos p
        INNER JOIN cl_clientes c ON p.cliente_id = c.id
        WHERE p.estado IN ('ACTIVO', 'MORA')
        ORDER BY p.fecha_creacion DESC
    """)
    suspend fun obtenerPrestamosActivosConCliente(): List<PrestamoConClienteInfo>

    @Query("UPDATE pr_prestamos SET estado = :nuevoEstado WHERE id = :prestamoId")
    suspend fun actualizarEstado(prestamoId: Int, nuevoEstado: String)

    @Query("UPDATE pr_prestamos SET saldo_pendiente = :nuevoSaldo WHERE id = :prestamoId")
    suspend fun actualizarSaldo(prestamoId: Int, nuevoSaldo: Double)
}

// Clase de datos para el JOIN (fuera de la interfaz)
data class PrestamoConClienteInfo(
    @Embedded val prestamo: Prestamo,
    val nombreCliente: String
)
