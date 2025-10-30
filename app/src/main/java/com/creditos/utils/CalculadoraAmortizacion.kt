package com.creditos.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.pow

class CalculadoraAmortizacion {

    fun generarTablaAmortizacion(
        montoPrincipal: Double,
        tasaInteresAnual: Double,
        numeroCuotas: Int,
        frecuenciaPago: String,
        tipoAmortizacion: String,
        fechaInicio: String
    ): List<CuotaCalculada> {

        val tasaPeriodica = calcularTasaPeriodica(tasaInteresAnual, frecuenciaPago)
        val fechasVencimiento = generarFechasVencimiento(fechaInicio, numeroCuotas, frecuenciaPago)

        return when (tipoAmortizacion.uppercase()) {
            "FRANCES" -> calcularFrances(montoPrincipal, tasaPeriodica, numeroCuotas, fechasVencimiento)
            "ALEMAN" -> calcularAleman(montoPrincipal, tasaPeriodica, numeroCuotas, fechasVencimiento)
            "AMERICANO" -> calcularAmericano(montoPrincipal, tasaPeriodica, numeroCuotas, fechasVencimiento)
            else -> calcularFrances(montoPrincipal, tasaPeriodica, numeroCuotas, fechasVencimiento)
        }
    }

    private fun calcularFrances(
        principal: Double,
        tasa: Double,
        cuotas: Int,
        fechas: List<String>
    ): List<CuotaCalculada> {
        val cuotaFija = principal * (tasa * (1 + tasa).pow(cuotas)) /
                ((1 + tasa).pow(cuotas) - 1)

        var saldo = principal
        val amortizaciones = mutableListOf<CuotaCalculada>()

        for (i in 1..cuotas) {
            val interes = saldo * tasa
            val capital = cuotaFija - interes
            saldo -= capital

            amortizaciones.add(
                CuotaCalculada(
                    numeroCuota = i,
                    fechaVencimiento = fechas[i - 1],
                    montoCapital = capital,
                    montoInteres = interes,
                    montoTotal = cuotaFija,
                    saldoRestante = if (saldo < 0.01) 0.0 else saldo
                )
            )
        }

        return amortizaciones
    }

    private fun calcularAleman(
        principal: Double,
        tasa: Double,
        cuotas: Int,
        fechas: List<String>
    ): List<CuotaCalculada> {
        val amortizacionCapital = principal / cuotas
        var saldo = principal
        val amortizaciones = mutableListOf<CuotaCalculada>()

        for (i in 1..cuotas) {
            val interes = saldo * tasa
            val capital = amortizacionCapital
            val cuotaTotal = capital + interes
            saldo -= capital

            amortizaciones.add(
                CuotaCalculada(
                    numeroCuota = i,
                    fechaVencimiento = fechas[i - 1],
                    montoCapital = capital,
                    montoInteres = interes,
                    montoTotal = cuotaTotal,
                    saldoRestante = if (saldo < 0.01) 0.0 else saldo
                )
            )
        }

        return amortizaciones
    }

    private fun calcularAmericano(
        principal: Double,
        tasa: Double,
        cuotas: Int,
        fechas: List<String>
    ): List<CuotaCalculada> {
        val amortizaciones = mutableListOf<CuotaCalculada>()

        // Cuotas de solo interés
        for (i in 1 until cuotas) {
            val interes = principal * tasa

            amortizaciones.add(
                CuotaCalculada(
                    numeroCuota = i,
                    fechaVencimiento = fechas[i - 1],
                    montoCapital = 0.0,
                    montoInteres = interes,
                    montoTotal = interes,
                    saldoRestante = principal
                )
            )
        }

        // Última cuota: interés + capital completo
        val ultimoInteres = principal * tasa
        amortizaciones.add(
            CuotaCalculada(
                numeroCuota = cuotas,
                fechaVencimiento = fechas[cuotas - 1],
                montoCapital = principal,
                montoInteres = ultimoInteres,
                montoTotal = principal + ultimoInteres,
                saldoRestante = 0.0
            )
        )

        return amortizaciones
    }

    private fun calcularTasaPeriodica(tasaAnual: Double, frecuencia: String): Double {
        return when (frecuencia.uppercase()) {
            "SEMANAL" -> tasaAnual / 52 / 100
            "QUINCENAL" -> tasaAnual / 24 / 100
            "MENSUAL" -> tasaAnual / 12 / 100
            "ANUAL" -> tasaAnual / 100
            else -> tasaAnual / 12 / 100
        }
    }

    private fun generarFechasVencimiento(
        fechaInicio: String,
        numeroCuotas: Int,
        frecuencia: String
    ): List<String> {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaBase = formato.parse(fechaInicio) ?: Date()
        val calendario = Calendar.getInstance()
        calendario.time = fechaBase

        val fechas = mutableListOf<String>()

        for (i in 1..numeroCuotas) {
            when (frecuencia.uppercase()) {
                "SEMANAL" -> calendario.add(Calendar.WEEK_OF_YEAR, 1)
                "QUINCENAL" -> calendario.add(Calendar.WEEK_OF_YEAR, 2)
                "MENSUAL" -> calendario.add(Calendar.MONTH, 1)
                "ANUAL" -> calendario.add(Calendar.YEAR, 1)
            }
            fechas.add(formato.format(calendario.time))
        }

        return fechas
    }

    fun calcularCuotaFija(
        montoPrincipal: Double,
        tasaInteresAnual: Double,
        numeroCuotas: Int,
        frecuenciaPago: String
    ): Double {
        val tasaPeriodica = calcularTasaPeriodica(tasaInteresAnual, frecuenciaPago)
        return montoPrincipal * (tasaPeriodica * (1 + tasaPeriodica).pow(numeroCuotas)) /
                ((1 + tasaPeriodica).pow(numeroCuotas) - 1)
    }

    fun calcularTotalIntereses(
        montoPrincipal: Double,
        tasaInteresAnual: Double,
        numeroCuotas: Int,
        frecuenciaPago: String,
        tipoAmortizacion: String
    ): Double {
        val tabla = generarTablaAmortizacion(
            montoPrincipal = montoPrincipal,
            tasaInteresAnual = tasaInteresAnual,
            numeroCuotas = numeroCuotas,
            frecuenciaPago = frecuenciaPago,
            tipoAmortizacion = tipoAmortizacion,
            fechaInicio = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )

        return tabla.sumOf { it.montoInteres }
    }

    data class CuotaCalculada(
        val numeroCuota: Int,
        val fechaVencimiento: String,
        val montoCapital: Double,
        val montoInteres: Double,
        val montoTotal: Double,
        val saldoRestante: Double
    )

    companion object {
        fun validarDatosPrestamo(
            montoPrincipal: Double,
            tasaInteres: Double,
            numeroCuotas: Int
        ): ResultadoValidacion {
            val errores = mutableListOf<String>()

            if (montoPrincipal <= 0) {
                errores.add("El monto principal debe ser mayor a 0")
            }

            if (tasaInteres < 0) {
                errores.add("La tasa de interés no puede ser negativa")
            }

            if (tasaInteres > 100) {
                errores.add("La tasa de interés es muy alta")
            }

            if (numeroCuotas <= 0) {
                errores.add("El número de cuotas debe ser mayor a 0")
            }

            if (numeroCuotas > 360) { // Máximo 30 años en cuotas mensuales
                errores.add("El número de cuotas es muy alto")
            }

            return ResultadoValidacion(
                esValido = errores.isEmpty(),
                errores = errores
            )
        }

        data class ResultadoValidacion(
            val esValido: Boolean,
            val errores: List<String>
        )
    }
}