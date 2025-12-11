//PaisDao.kt
package com.creditos.data.dao

import androidx.room.*
import com.creditos.data.entities.Pais
import kotlinx.coroutines.flow.Flow

@Dao
interface PaisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pais: Pais): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(paises: List<Pais>)

    @Query("SELECT * FROM cl_paises ORDER BY orden")
    fun getAllPaises(): Flow<List<Pais>>

    @Query("SELECT * FROM cl_paises WHERE id = :id")
    suspend fun getPaisById(id: Int): Pais?

    @Query("SELECT * FROM cl_paises WHERE codigo = :codigo")
    suspend fun getPaisByCodigo(codigo: String): Pais?

    @Query("SELECT COUNT(*) FROM cl_paises")
    suspend fun count(): Int
}