//PrestamoRepository.kt
package com.creditos.data.repository

import com.creditos.data.dao.PrestamoDao
import com.creditos.data.dao.CuotaDao
import com.creditos.data.dao.PrestamoConClienteInfo
import com.creditos.data.entities.Cuota
import com.creditos.data.entities.Prestamo
import com.creditos.utils.CalculadoraAmortizacion
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

        // Totales
        val totalIntereses = tablaCalculada.sumOf { it.montoInteres }
        val montoTotalPagar = montoPrincipal + totalIntereses

        // Crear préstamo (usar snake_case de la entidad Prestamo)
        val prestamo = Prestamo(
            clienteId = clienteId,
            montoPrincipal = montoPrincipal,
            tasaInteres = tasaInteres,
            numeroCuotas = numeroCuotas,
            frecuenciaPago = frecuenciaPago,
            tipoAmortizacion = tipoAmortizacion,
            fechaInicio = fechaInicio,
            fechaPrimerPago = tablaCalculada.first().fechaVencimiento,
            estado = "ACTIVO",
            saldoPendiente = montoPrincipal,
            montoTotalPagar = montoTotalPagar,
            totalIntereses = totalIntereses,
            notas = notas,
            fechaCreacion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            fechaFinalizacion = null
        )

        val prestamoId = prestamoDao.insertar(prestamo)

        // Crear cuotas
        val cuotas = tablaCalculada.mapIndexed { index, calculada ->
            Cuota(
                prestamoId = prestamoId.toInt(),
                numeroCuota = index + 1,
                fechaVencimiento = calculada.fechaVencimiento,
                montoCapital = calculada.montoCapital,
                montoInteres = calculada.montoInteres,
                montoTotal = calculada.montoTotal,
                saldoRestante = calculada.saldoRestante
            )
        }

        cuotaDao.insertarTodos(cuotas)

        return prestamoId
    }

    suspend fun obtenerPrestamoPorId(prestamoId: Int): Prestamo? =
        prestamoDao.obtenerPorId(prestamoId)

    suspend fun obtenerPrestamosPorCliente(clienteId: Int): List<Prestamo> =
        prestamoDao.obtenerPorCliente(clienteId)

    suspend fun obtenerPrestamosActivos(): List<PrestamoConClienteInfo> =
        prestamoDao.obtenerPrestamosActivosConCliente()

    suspend fun obtenerPrestamosPorEstado(estado: String): List<Prestamo> =
        prestamoDao.obtenerPorEstado(estado)

    suspend fun actualizarEstadoPrestamo(prestamoId: Int, nuevoEstado: String) =
        prestamoDao.actualizarEstado(prestamoId, nuevoEstado)

    suspend fun actualizarSaldoPrestamo(prestamoId: Int, nuevoSaldo: Double) =
        prestamoDao.actualizarSaldo(prestamoId, nuevoSaldo)

    suspend fun obtenerResumenPrestamo(prestamoId: Int): ResumenPrestamo {
        val prestamo = prestamoDao.obtenerPorId(prestamoId)
            ?: throw Exception("Préstamo no encontrado")
        val cuotas = cuotaDao.obtenerPorPrestamo(prestamoId)

        val cuotasPagadas = cuotas.count { it.estado == "PAGADA" }
        val cuotasPendientes = cuotas.count { it.estado == "PENDIENTE" }
        val cuotasVencidas = cuotas.count { it.estado == "VENCIDA" }
        val totalPagado = cuotas.filter { it.estado == "PAGADA" }.sumOf { it.montoPagado }
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
