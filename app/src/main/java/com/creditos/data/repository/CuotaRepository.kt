package com.creditos.data.repository

import com.creditos.data.dao.CuotaDao
import com.creditos.data.entities.Cuota
import javax.inject.Inject

class CuotaRepository @Inject constructor(
    private val cuotaDao: CuotaDao
) {

    suspend fun obtenerCuotasPorPrestamo(prestamoId: Int): List<Cuota> {
        return cuotaDao.obtenerPorPrestamo(prestamoId)
    }

    suspend fun obtenerProximaCuotaPendiente(prestamoId: Int): Cuota? {
        return cuotaDao.obtenerProximaCuotaPendiente(prestamoId)
    }

    suspend fun obtenerCuotasPendientes(prestamoId: Int): List<Cuota> {
        return cuotaDao.obtenerCuotasPendientes(prestamoId)
    }

    suspend fun insertarCuotas(cuotas: List<Cuota>) {
        cuotaDao.insertarTodos(cuotas)
    }

    suspend fun actualizarCuota(cuota: Cuota) {
        cuotaDao.actualizar(cuota)
    }
}
