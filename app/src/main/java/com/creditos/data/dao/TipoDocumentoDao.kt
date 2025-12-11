package com.creditos.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.creditos.data.entities.TipoDocumento
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoDocumentoDao {

    @Query("SELECT * FROM cl_tipos_documento ORDER BY id")
    suspend fun obtenerActivos(): List<TipoDocumento>

    // Útil para lógica de negocio / filtros por código ("DNI", "NIE", etc.)
    @Query("SELECT * FROM cl_tipos_documento WHERE codigo = :codigo LIMIT 1")
    suspend fun obtenerPorCodigo(codigo: String): TipoDocumento?
}
