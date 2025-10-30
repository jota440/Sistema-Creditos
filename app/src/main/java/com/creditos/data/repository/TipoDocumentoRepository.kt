package com.creditos.data.repository

import com.creditos.data.entities.TipoDocumento
import com.creditos.data.dao.TipoDocumentoDao
import javax.inject.Inject

class TipoDocumentoRepository @Inject constructor(
    private val tipoDocumentoDao: TipoDocumentoDao
) {

    suspend fun obtenerTiposDocumentoActivos(): List<TipoDocumento> {
        return tipoDocumentoDao.obtenerActivos()
    }
}