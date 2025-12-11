//ComunidadRepository.kt
package com.creditos.data.repository

import com.creditos.data.dao.ComunidadDao
import com.creditos.data.entities.Comunidad
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ComunidadRepository @Inject constructor(
    private val comunidadDao: ComunidadDao
) {
    fun getAllComunidades(): Flow<List<Comunidad>> = comunidadDao.getAllComunidades()

    suspend fun getComunidadById(id: Int): Comunidad? = comunidadDao.getComunidadById(id)

    suspend fun getComunidadByNombre(nombre: String): Comunidad? = comunidadDao.getComunidadByNombre(nombre)

    suspend fun count(): Int = comunidadDao.count()
}