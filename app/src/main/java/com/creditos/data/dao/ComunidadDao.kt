//ComunidadDao.kt
package com.creditos.data.dao

import androidx.room.*
import com.creditos.data.entities.Comunidad
import kotlinx.coroutines.flow.Flow

@Dao
interface ComunidadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comunidad: Comunidad): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comunidades: List<Comunidad>)

    @Query("SELECT * FROM cl_comunidades ORDER BY comunidad")
    fun getAllComunidades(): Flow<List<Comunidad>>

    @Query("SELECT * FROM cl_comunidades WHERE id = :id")
    suspend fun getComunidadById(id: Int): Comunidad?

    @Query("SELECT * FROM cl_comunidades WHERE comunidad = :nombre")
    suspend fun getComunidadByNombre(nombre: String): Comunidad?

    @Query("SELECT COUNT(*) FROM cl_comunidades")
    suspend fun count(): Int
}