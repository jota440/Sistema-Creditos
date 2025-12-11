//PaisRepository.kt
package com.creditos.data.repository

import com.creditos.data.dao.PaisDao
import com.creditos.data.entities.Pais
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaisRepository @Inject constructor(
    private val paisDao: PaisDao
) {
    fun getAllPaises(): Flow<List<Pais>> = paisDao.getAllPaises()

    suspend fun getPaisById(id: Int): Pais? = paisDao.getPaisById(id)

    suspend fun getPaisByCodigo(codigo: String): Pais? = paisDao.getPaisByCodigo(codigo)

    suspend fun count(): Int = paisDao.count()
}