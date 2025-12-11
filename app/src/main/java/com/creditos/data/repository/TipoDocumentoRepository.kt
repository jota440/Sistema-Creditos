//TipoDocumentoRepository.kt
package com.creditos.data.repository

import com.creditos.data.entities.TipoDocumento
import com.creditos.data.dao.TipoDocumentoDao
import javax.inject.Inject

class TipoDocumentoRepository @Inject constructor(
    private val tipoDocumentoDao: TipoDocumentoDao
) {

    suspend fun obtenerTiposDocumentoActivos(): List<TipoDocumento> {
        return try {
            val tipos = tipoDocumentoDao.obtenerActivos()
            android.util.Log.d("TipoDocumentoRepo", "Tipos de documento cargados: ${tipos.size}")
            tipos.forEach { tipo ->
                android.util.Log.d("TipoDocumentoRepo", "Tipo: ${tipo.codigo} - ${tipo.descripcion}")
            }
            tipos
        } catch (e: Exception) {
            android.util.Log.e("TipoDocumentoRepo", "Error cargando tipos documento", e)
            emptyList()
        }
    }

    // NUEVO: obtener un tipo por su código (DNI, NIE, CIF, etc.)
    suspend fun obtenerPorCodigo(codigo: String): TipoDocumento? {
        return try {
            tipoDocumentoDao.obtenerPorCodigo(codigo)
        } catch (e: Exception) {
            android.util.Log.e(
                "TipoDocumentoRepo",
                "Error obteniendo tipo documento por código $codigo",
                e
            )
            null
        }
    }

    // Método para debug (lo mantengo tal cual)
    suspend fun obtenerTodosTiposDocumento(): List<TipoDocumento> {
        return try {
            val tipos = tipoDocumentoDao.obtenerActivos()
            android.util.Log.d("TipoDocumentoRepo", "Todos los tipos: ${tipos.size}")
            tipos
        } catch (e: Exception) {
            android.util.Log.e("TipoDocumentoRepo", "Error cargando todos los tipos", e)
            emptyList()
        }
    }
}
