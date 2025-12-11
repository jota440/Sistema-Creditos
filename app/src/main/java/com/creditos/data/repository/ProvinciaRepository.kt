package com.creditos.data.repository

import com.creditos.data.dao.ProvinciaDao
import com.creditos.data.entities.Provincia
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProvinciaRepository @Inject constructor(
    private val provinciaDao: ProvinciaDao
) {
    fun getAllProvincias(): Flow<List<Provincia>> = provinciaDao.getAllProvincias()

    suspend fun getProvinciaById(id: Int): Provincia? = provinciaDao.getProvinciaById(id)

    suspend fun getProvinciaByCodigoPostal(codigoPostal: String): Provincia? =
        provinciaDao.getProvinciaByCodigoPostal(codigoPostal)

    fun getProvinciasByComunidad(comunidadId: Int): Flow<List<Provincia>> =
        provinciaDao.getProvinciasByComunidad(comunidadId)

    suspend fun count(): Int = provinciaDao.count()
}