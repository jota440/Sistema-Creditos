//ClienteRepository.kt
package com.creditos.data.repository

import com.creditos.data.entities.Cliente
import com.creditos.data.dao.ClienteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClienteRepository @Inject constructor(
    private val clienteDao: ClienteDao
) {

    suspend fun obtenerTodosClientes(): List<Cliente> {
        return clienteDao.obtenerTodos()
    }

    suspend fun obtenerClientePorId(id: Int): Cliente? {
        return clienteDao.obtenerPorId(id)
    }

    suspend fun insertarCliente(
        nombre: String,
        apellido: String,
        tipoDocumentoId: Int,
        numeroDocumento: String,
        telefonoPrincipal: String,
        email: String? = null,
        fechaRegistro: String
    ) {
        val cliente = Cliente(
            nombre = nombre,
            apellido = apellido,
            tipoDocumentoId = tipoDocumentoId,
            numeroDocumento = numeroDocumento,
            telefonoPrincipal = telefonoPrincipal,
            email = email,
            fechaRegistro = fechaRegistro
        )
        clienteDao.insertar(cliente)
    }

    // NUEVO: Método que inserta y devuelve el ID como Int
    suspend fun insertarClienteYDevolverId(
        nombre: String,
        apellido: String,
        tipoDocumentoId: Int,
        numeroDocumento: String,
        telefonoPrincipal: String,
        email: String? = null,
        fechaRegistro: String
    ): Int {
        val cliente = Cliente(
            nombre = nombre,
            apellido = apellido,
            tipoDocumentoId = tipoDocumentoId,
            numeroDocumento = numeroDocumento,
            telefonoPrincipal = telefonoPrincipal,
            email = email,
            fechaRegistro = fechaRegistro
        )
        val idLong = clienteDao.insertarYDevolverId(cliente)
        return idLong.toInt() // Convertir Long a Int
    }

    suspend fun buscarClientes(query: String): List<Cliente> {
        return clienteDao.buscarPorNombreODocumento("%$query%")
    }

    suspend fun obtenerClientesActivos(): List<Cliente> {
        return clienteDao.obtenerActivos()
    }
}