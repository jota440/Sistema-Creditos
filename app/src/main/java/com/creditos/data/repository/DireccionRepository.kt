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

    suspend fun marcarComoPredeterminada(direccionId: Int, clienteId: Int) {
        direccionDao.clearDireccionesPrincipales(clienteId)
        val direccion = direccionDao.getById(direccionId)
        direccion?.let { dir ->
            // CORREGIDO: Usar constructor completo en lugar de copy()
            val direccionActualizada = Direccion(
                id = dir.id,
                clienteId = dir.clienteId,
                tipoDireccion = dir.tipoDireccion,
                calle = dir.calle,
                numero = dir.numero,
                piso = dir.piso,
                puerta = dir.puerta,
                codigoPostal = dir.codigoPostal,
                ciudad = dir.ciudad,
                provincia = dir.provincia,
                pais = dir.pais,
                predeterminada = true,  // ← Cambiar a true
                notas = dir.notas,
                fechaCreacion = dir.fechaCreacion
            )
            direccionDao.update(direccionActualizada)
        }
    }
}