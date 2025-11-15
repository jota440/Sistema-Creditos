//ClienteDao.kt
package com.creditos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.creditos.data.entities.Cliente
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Query("SELECT * FROM cl_clientes ORDER BY nombre, apellido")
    suspend fun obtenerTodos(): List<Cliente>

    @Query("SELECT * FROM cl_clientes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Cliente?

    @Insert
    suspend fun insertar(cliente: Cliente)

    @Insert
    suspend fun insertarYDevolverId(cliente: Cliente): Long

    @Query("SELECT * FROM cl_clientes WHERE nombre LIKE :query OR apellido LIKE :query OR numero_documento LIKE :query")
    suspend fun buscarPorNombreODocumento(query: String): List<Cliente>

    @Query("SELECT * FROM cl_clientes WHERE activo = 1 ORDER BY nombre, apellido")
    suspend fun obtenerActivos(): List<Cliente>
}
