//CodigoPostalDao.kt
package com.creditos.data.dao

import androidx.room.*
import com.creditos.data.entities.CodigoPostal
import kotlinx.coroutines.flow.Flow

@Dao
interface CodigoPostalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(codigoPostal: CodigoPostal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(codigosPostales: List<CodigoPostal>)

    @Query("SELECT * FROM cl_codigo_postales WHERE codigo_postal = :codigo")
    suspend fun getByCodigoPostal(codigo: String): CodigoPostal?

    @Query("SELECT * FROM cl_codigo_postales WHERE ciudad LIKE '%' || :ciudad || '%' ORDER BY ciudad")
    fun searchByCiudad(ciudad: String): Flow<List<CodigoPostal>>

    @Query("SELECT * FROM cl_codigo_postales WHERE codigo_postal LIKE :prefijo || '%' ORDER BY codigo_postal")
    fun getByPrefijo(prefijo: String): Flow<List<CodigoPostal>>

    @Query("SELECT COUNT(*) FROM cl_codigo_postales")
    suspend fun count(): Int
}