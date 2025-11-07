//DireccionDao.kt
package com.creditos.data.dao

import androidx.room.*
import com.creditos.data.entities.Direccion
import kotlinx.coroutines.flow.Flow

@Dao
interface DireccionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(direccion: Direccion): Long  // Long porque Room retorna Long en inserts

    @Update
    suspend fun update(direccion: Direccion): Int

    @Delete
    suspend fun delete(direccion: Direccion): Int

    @Query("DELETE FROM cl_direcciones WHERE id = :id")
    suspend fun deleteById(id: Int): Int  // ← Cambiar a Int

    @Query("SELECT * FROM cl_direcciones WHERE id = :id")
    suspend fun getById(id: Int): Direccion?  // ← Cambiar a Int

    @Query("SELECT * FROM cl_direcciones WHERE cliente_id = :clienteId ORDER BY predeterminada DESC, tipo_direccion")
    fun getByClienteId(clienteId: Int): Flow<List<Direccion>>  // ← Cambiar a Int

    @Query("SELECT * FROM cl_direcciones WHERE cliente_id = :clienteId AND tipo_direccion = :tipo")
    suspend fun getByClienteIdAndTipo(clienteId: Int, tipo: String): Direccion?  // ← Cambiar a Int

    @Query("SELECT * FROM cl_direcciones WHERE cliente_id = :clienteId AND predeterminada = 1")
    suspend fun getDireccionPrincipal(clienteId: Int): Direccion?  // ← Cambiar a Int

    @Query("UPDATE cl_direcciones SET predeterminada = 0 WHERE cliente_id = :clienteId")
    suspend fun clearDireccionesPrincipales(clienteId: Int): Int  // ← Cambiar a Int

    @Query("SELECT COUNT(*) FROM cl_direcciones WHERE cliente_id = :clienteId")
    suspend fun countByClienteId(clienteId: Int): Int  // ← Cambiar a Int
}