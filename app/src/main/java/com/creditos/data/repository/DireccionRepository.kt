//DireccionRepository.kt
package com.creditos.data.repository

import com.creditos.data.dao.DireccionDao
import com.creditos.data.entities.Direccion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DireccionRepository @Inject constructor(
    private val direccionDao: DireccionDao
) {

    suspend fun insert(direccion: Direccion): Long {
        if (direccion.predeterminada) {
            direccionDao.clearDireccionesPrincipales(direccion.clienteId)
        }
        return direccionDao.insert(direccion)
    }

    suspend fun update(direccion: Direccion): Int {
        if (direccion.predeterminada) {
            direccionDao.clearDireccionesPrincipales(direccion.clienteId)
        }
        return direccionDao.update(direccion)
    }

    suspend fun delete(direccion: Direccion): Int {
        return direccionDao.delete(direccion)
    }

    suspend fun deleteById(id: Int): Int {  // ← Cambiar a Int
        return direccionDao.deleteById(id)
    }

    suspend fun getById(id: Int): Direccion? {  // ← Cambiar a Int
        return direccionDao.getById(id)
    }

    fun getByClienteId(clienteId: Int): Flow<List<Direccion>> {
        return direccionDao.getByClienteId(clienteId)
    }

    suspend fun getByClienteIdAndTipo(clienteId: Int, tipo: String): Direccion? {
        return direccionDao.getByClienteIdAndTipo(clienteId, tipo)
    }

    suspend fun getDireccionPrincipal(clienteId: Int): Direccion? {
        return direccionDao.getDireccionPrincipal(clienteId)
    }

    suspend fun countByClienteId(clienteId: Int): Int {
        return direccionDao.countByClienteId(clienteId)
    }
}