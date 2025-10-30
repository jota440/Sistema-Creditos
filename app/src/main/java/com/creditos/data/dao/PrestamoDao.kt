package com.creditos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Embedded
import com.creditos.data.entities.Prestamo
import kotlinx.coroutines.flow.Flow

@Dao
interface PrestamoDao {

    @Insert
    suspend fun insertar(prestamo: Prestamo): Long

    @Update
    suspend fun actualizar(prestamo: Prestamo)

    @Query("SELECT * FROM pr_prestamos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Prestamo?

    @Query("SELECT * FROM pr_prestamos WHERE clienteId = :clienteId ORDER BY fechaCreacion DESC")
    suspend fun obtenerPorCliente(clienteId: Int): List<Prestamo>

    @Query("SELECT * FROM pr_prestamos WHERE estado = :estado ORDER BY fechaCreacion DESC")
    suspend fun obtenerPorEstado(estado: String): List<Prestamo>

    @Query("""
        SELECT p.*, c.nombre || ' ' || c.apellido as nombreCliente 
        FROM pr_prestamos p 
        INNER JOIN cl_clientes c ON p.clienteId = c.id 
        WHERE p.estado IN ('ACTIVO', 'MORA') 
        ORDER BY p.fechaCreacion DESC
    """)
    suspend fun obtenerPrestamosActivosConCliente(): List<PrestamoConClienteInfo>

    @Query("UPDATE pr_prestamos SET estado = :nuevoEstado WHERE id = :prestamoId")
    suspend fun actualizarEstado(prestamoId: Int, nuevoEstado: String)

    @Query("UPDATE pr_prestamos SET saldoPendiente = :nuevoSaldo WHERE id = :prestamoId")
    suspend fun actualizarSaldo(prestamoId: Int, nuevoSaldo: Double)
}

// Clase de datos para el query con JOIN
// Debe estar FUERA de la interfaz del DAO
data class PrestamoConClienteInfo(
    @Embedded val prestamo: Prestamo,
    val nombreCliente: String
)