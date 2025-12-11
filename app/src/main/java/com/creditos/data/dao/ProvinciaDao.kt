//ProvinciaDao.kt
package com.creditos.data.dao

import androidx.room.*
import com.creditos.data.entities.Provincia
import kotlinx.coroutines.flow.Flow

@Dao
interface ProvinciaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(provincia: Provincia): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(provincias: List<Provincia>)

    @Query("SELECT * FROM cl_provincias ORDER BY provincia")
    fun getAllProvincias(): Flow<List<Provincia>>

    @Query("SELECT * FROM cl_provincias WHERE id = :id")
    suspend fun getProvinciaById(id: Int): Provincia?

    @Query("SELECT * FROM cl_provincias WHERE id_codigo_postal = :codigoPostal")
    suspend fun getProvinciaByCodigoPostal(codigoPostal: String): Provincia?

    @Query("SELECT * FROM cl_provincias WHERE id_comunidad = :comunidadId ORDER BY provincia")
    fun getProvinciasByComunidad(comunidadId: Int): Flow<List<Provincia>>

    @Query("SELECT COUNT(*) FROM cl_provincias")
    suspend fun count(): Int
}