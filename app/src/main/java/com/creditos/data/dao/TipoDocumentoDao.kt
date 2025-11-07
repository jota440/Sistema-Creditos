package com.creditos.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.creditos.data.entities.TipoDocumento
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoDocumentoDao {

    @Query("SELECT * FROM cl_tipos_documento")
    suspend fun obtenerActivos(): List<TipoDocumento>
}
