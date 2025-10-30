package com.creditos.data.repository

import com.creditos.data.dao.PrestamoDao
import com.creditos.data.dao.CuotaDao
import com.creditos.data.entities.Prestamo
import com.creditos.data.entities.Cuota
import com.creditos.utils.CalculadoraAmortizacion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PrestamoRepository @Inject constructor(
    private val prestamoDao: PrestamoDao,
    private val cuotaDao: CuotaDao
) {

    suspend fun crearPrestamoConCuotas(
        clienteId: Int,
        montoPrincipal: Double,
        tasaInteres: Double,
        numeroCuotas: Int,
        frecuenciaPago: String,
        tipoAmortizacion: String,
        fechaInicio: String,
        notas: String? = null
    ): Long {
        // Calcular tabla de amortización
        val calculadora = CalculadoraAmortizacion()
        val tablaCalculada = calculadora.generarTablaAmortizacion(
            montoPrincipal = montoPrincipal,
            tasaInteresAnual = tasaInteres,
            numeroCuotas = numeroCuotas,
            frecuenciaPago = frecuenciaPago,
            tipoAmortizacion = tipoAmortizacion,
            fechaInicio = fechaInicio
        )

        // Calcular totales
        val totalIntereses = tablaCalculada.sumOf { it.montoInteres }
        val montoTotalPagar = montoPrincipal + totalIntereses

        // Crear préstamo
        val prestamo = Prestamo(
            clienteId = clienteId,
            montoPrincipal = montoPrincipal,
            tasaInteres = tasaInteres,
            numeroCuotas = numeroCuotas,
            frecuenciaPago = frecuenciaPago,
            tipoAmortizacion = tipoAmortizacion,
            fechaInicio = fechaInicio,
            fechaPrimerPago = tablaCalculada.first().fechaVencimiento,
            saldoPendiente = montoPrincipal,
            montoTotalPagar = montoTotalPagar,
            totalIntereses = totalIntereses,
            notas = notas,
            fechaCreacion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        )

        val prestamoId = prestamoDao.insertar(prestamo)

        // Crear cuotas
        val cuotas = tablaCalculada.mapIndexed { index, calculada ->
            Cuota(
                prestamo_id = prestamoId.toInt(),
                numero_cuota = index + 1,
                fecha_vencimiento = calculada.fechaVencimiento,
                monto_capital = calculada.montoCapital,
                monto_interes = calculada.montoInteres,
                monto_total = calculada.montoTotal,
                saldo_restante = calculada.saldoRestante
            )
        }

        cuotaDao.insertarTodos(cuotas)

        return prestamoId
    }

    suspend fun obtenerPrestamoPorId(prestamoId: Int): Prestamo? {
        return prestamoDao.obtenerPorId(prestamoId)
    }

    suspend fun obtenerPrestamosPorCliente(clienteId: Int): List<Prestamo> {
        return prestamoDao.obtenerPorCliente(clienteId)
    }

    suspend fun obtenerPrestamosActivos(): List<PrestamoDao.PrestamoConCliente> {
        return prestamoDao.obtenerPrestamosActivosConCliente()
    }

    suspend fun obtenerPrestamosPorEstado(estado: String): List<Prestamo> {
        return prestamoDao.obtenerPorEstado(estado)
    }

    suspend fun actualizarEstadoPrestamo(prestamoId: Int, nuevoEstado: String) {
        prestamoDao.actualizarEstado(prestamoId, nuevoEstado)
    }

    suspend fun actualizarSaldoPrestamo(prestamoId: Int, nuevoSaldo: Double) {
        prestamoDao.actualizarSaldo(prestamoId, nuevoSaldo)
    }

    suspend fun obtenerResumenPrestamo(prestamoId: Int): ResumenPrestamo {
        val prestamo = prestamoDao.obtenerPorId(prestamoId) ?: throw Exception("Préstamo no encontrado")
        val cuotas = cuotaDao.obtenerPorPrestamo(prestamoId)

        val cuotasPagadas = cuotas.count { it.estado == "PAGADA" }
        val cuotasPendientes = cuotas.count { it.estado == "PENDIENTE" }
        val cuotasVencidas = cuotas.count { it.estado == "VENCIDA" }
        val totalPagado = cuotas.filter { it.estado == "PAGADA" }.sumOf { it.monto_pagado }
        val proximaCuota = cuotaDao.obtenerProximaCuotaPendiente(prestamoId)

        return ResumenPrestamo(
            prestamo = prestamo,
            totalPagado = totalPagado,
            saldoPendiente = prestamo.saldoPendiente,
            cuotasPagadas = cuotasPagadas,
            cuotasPendientes = cuotasPendientes,
            cuotasVencidas = cuotasVencidas,
            proximaCuota = proximaCuota
        )
    }

    data class ResumenPrestamo(
        val prestamo: Prestamo,
        val totalPagado: Double,
        val saldoPendiente: Double,
        val cuotasPagadas: Int,
        val cuotasPendientes: Int,
        val cuotasVencidas: Int,
        val proximaCuota: Cuota?
    )
}