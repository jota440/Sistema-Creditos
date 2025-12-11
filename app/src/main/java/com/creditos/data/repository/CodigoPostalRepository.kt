
//CodigoPostalRepository.kt
package com.creditos.data.repository

import com.creditos.data.dao.CodigoPostalDao
import com.creditos.data.entities.CodigoPostal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CodigoPostalRepository @Inject constructor(
    private val codigoPostalDao: CodigoPostalDao
) {
    suspend fun getByCodigoPostal(codigo: String): CodigoPostal? =
        codigoPostalDao.getByCodigoPostal(codigo)

    fun searchByCiudad(ciudad: String): Flow<List<CodigoPostal>> =
        codigoPostalDao.searchByCiudad(ciudad)

    fun getByPrefijo(prefijo: String): Flow<List<CodigoPostal>> =
        codigoPostalDao.getByPrefijo(prefijo)

    suspend fun count(): Int = codigoPostalDao.count()
}