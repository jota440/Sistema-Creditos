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

    @Query(
        """
        SELECT * FROM cl_clientes 
        WHERE nombre LIKE :query OR apellido LIKE :query OR numero_documento LIKE :query
        """
    )
    suspend fun buscarPorNombreODocumento(query: String): List<Cliente>

    @Query("SELECT * FROM cl_clientes WHERE activo = 1 ORDER BY nombre, apellido")
    suspend fun obtenerActivos(): List<Cliente>

    // 🔹 NUEVO: búsqueda SOLO por apellido (ej: 'hernan%')
    @Query(
        """
        SELECT * FROM cl_clientes 
        WHERE apellido LIKE :apellidoPattern
        ORDER BY apellido, nombre
        """
    )
    suspend fun buscarPorApellido(apellidoPattern: String): List<Cliente>

    // 🔹 NUEVO: versiones reactivas para listas
    @Query("SELECT * FROM cl_clientes ORDER BY nombre, apellido")
    fun observarTodos(): Flow<List<Cliente>>

    @Query("SELECT * FROM cl_clientes WHERE activo = 1 ORDER BY nombre, apellido")
    fun observarActivos(): Flow<List<Cliente>>

    // 🔹 NUEVO: búsqueda avanzada con filtros opcionales
    @Query(
        """
        SELECT * 
        FROM cl_clientes
        WHERE (:tipoDocumentoId IS NULL OR tipo_documento_id = :tipoDocumentoId)
          AND (:texto IS NULL OR (nombre LIKE :texto OR apellido LIKE :texto OR numero_documento LIKE :texto))
          AND (:email IS NULL OR email LIKE :email)
          AND (:telefono IS NULL OR telefono_principal LIKE :telefono)
          AND (:soloActivos IS NULL OR (:soloActivos = 0 OR activo = 1))
        ORDER BY nombre, apellido
        """
    )
    suspend fun buscarAvanzado(
        tipoDocumentoId: Int?,
        texto: String?,
        email: String?,
        telefono: String?,
        soloActivos: Int?
    ): List<Cliente>
}
