//DireccionDao.kt
package com.creditos.data.dao

import androidx.room.*
import com.creditos.data.entities.Direccion
import kotlinx.coroutines.flow.Flow

@Dao
interface DireccionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(direccion: Direccion): Long  // Room retorna Long

    @Update
    suspend fun update(direccion: Direccion): Int

    @Delete
    suspend fun delete(direccion: Direccion): Int

    @Query("DELETE FROM cl_direcciones WHERE id = :id")
    suspend fun deleteById(id: Long): Int  // CAMBIAR a Long

    @Query("SELECT * FROM cl_direcciones WHERE id = :id")
    suspend fun getById(id: Long): Direccion?  // CAMBIAR a Long

    @Query("SELECT * FROM cl_direcciones WHERE cliente_id = :clienteId ORDER BY predeterminada DESC, tipo_direccion")
    fun getByClienteId(clienteId: Int): Flow<List<Direccion>>

    @Query("SELECT * FROM cl_direcciones WHERE cliente_id = :clienteId AND tipo_direccion = :tipo")
    suspend fun getByClienteIdAndTipo(clienteId: Int, tipo: String): Direccion?

    @Query("SELECT * FROM cl_direcciones WHERE cliente_id = :clienteId AND predeterminada = 1")
    suspend fun getDireccionPrincipal(clienteId: Int): Direccion?

    @Query("UPDATE cl_direcciones SET predeterminada = 0 WHERE cliente_id = :clienteId")
    suspend fun clearDireccionesPrincipales(clienteId: Int): Int

    @Query("SELECT COUNT(*) FROM cl_direcciones WHERE cliente_id = :clienteId")
    suspend fun countByClienteId(clienteId: Int): Int
}