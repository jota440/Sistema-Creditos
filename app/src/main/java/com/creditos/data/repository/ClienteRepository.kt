//ClienteRepository.kt
package com.creditos.data.repository

import com.creditos.data.entities.Cliente
import com.creditos.data.dao.ClienteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import java.util.Date

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
        fechaRegistro: Date
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

    // Inserta y devuelve el ID como Int (ya lo tenías)
    suspend fun insertarClienteYDevolverId(
        nombre: String,
        apellido: String,
        tipoDocumentoId: Int,
        numeroDocumento: String,
        telefonoPrincipal: String,
        email: String? = null,
        fechaRegistro: Date
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
        return idLong.toInt()
    }

    suspend fun buscarClientes(query: String): List<Cliente> {
        return clienteDao.buscarPorNombreODocumento("%$query%")
    }

    suspend fun obtenerClientesActivos(): List<Cliente> {
        return clienteDao.obtenerActivos()
    }

    // 🔹 NUEVO: Flows para pantallas reactivas
    fun observarTodosClientes(): Flow<List<Cliente>> {
        return clienteDao.observarTodos()
    }

    fun observarClientesActivos(): Flow<List<Cliente>> {
        return clienteDao.observarActivos()
    }

    suspend fun buscarClientesPorApellidoPattern(apellidoPattern: String): List<Cliente> {
        return clienteDao.buscarPorApellido(apellidoPattern)
    }

    suspend fun buscarClientesPorApellidoPrefijo(apellido: String): List<Cliente> {
        return clienteDao.buscarPorApellido("${apellido.trim()}%")
    }

    // 🔹 NUEVO: búsqueda avanzada con filtros opcionales
    suspend fun buscarClientesAvanzado(
        tipoDocumentoId: Int? = null,
        textoLibre: String? = null,
        email: String? = null,
        telefono: String? = null,
        soloActivos: Boolean? = null
    ): List<Cliente> {
        val textoLike = textoLibre
            ?.takeIf { it.isNotBlank() }
            ?.let { "%$it%" }

        val emailLike = email
            ?.takeIf { it.isNotBlank() }
            ?.let { "%$it%" }

        val telefonoLike = telefono
            ?.takeIf { it.isNotBlank() }
            ?.let { "%$it%" }

        val soloActivosFlag = soloActivos?.let { activo ->
            if (activo) 1 else 0
        }

        return clienteDao.buscarAvanzado(
            tipoDocumentoId = tipoDocumentoId,
            texto = textoLike,
            email = emailLike,
            telefono = telefonoLike,
            soloActivos = soloActivosFlag
        )
    }
}
