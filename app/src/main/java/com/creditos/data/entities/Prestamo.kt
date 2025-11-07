package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(
    tableName = "pr_prestamos",
    indices = [
        Index(value = ["cliente_id"]),
        Index(value = ["estado"]),
        Index(value = ["saldo_pendiente"]),
        Index(value = ["fecha_finalizacion"],)
    ],
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["cliente_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Prestamo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "cliente_id")
    val clienteId: Int,
    @ColumnInfo(name = "monto_principal")
    val montoPrincipal: Double,
    @ColumnInfo(name = "tasa_interes")
    val tasaInteres: Double,
    @ColumnInfo(name = "numero_cuotas")
    val numeroCuotas: Int,
    @ColumnInfo(name = "frecuencia_pago")
    val frecuenciaPago: String,
    @ColumnInfo(name = "tipo_amortizacion")
    val tipoAmortizacion: String = "FRANCES",
    @ColumnInfo(name = "fecha_inicio")
    val fechaInicio: String,
    @ColumnInfo(name = "fecha_primer_pago")
    val fechaPrimerPago: String,
    val estado: String = "ACTIVO",
    @ColumnInfo(name = "saldo_pendiente")
    val saldoPendiente: Double,
    @ColumnInfo(name = "monto_total_pagar")
    val montoTotalPagar: Double,
    @ColumnInfo(name = "total_intereses")
    val totalIntereses: Double,
    val notas: String? = null,
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String = Date().toString(),
    @ColumnInfo(name = "fecha_finalizacion")
    val fechaFinalizacion: String? = null
) {

    fun obtenerFrecuenciaPagoTexto(): String {
        return when (frecuenciaPago.uppercase()) {
            "SEMANAL" -> "Semanal"
            "QUINCENAL" -> "Quincenal"
            "MENSUAL" -> "Mensual"
            "ANUAL" -> "Anual"
            else -> frecuenciaPago
        }
    }

    fun obtenerTipoAmortizacionTexto(): String {
        return when (tipoAmortizacion.uppercase()) {
            "FRANCES" -> "Francés"
            "ALEMAN" -> "Alemán"
            "AMERICANO" -> "Americano"
            else -> tipoAmortizacion
        }
    }

    fun obtenerEstadoTexto(): String {
        return when (estado.uppercase()) {
            "ACTIVO" -> "Activo"
            "FINALIZADO" -> "Finalizado"
            "MORA" -> "En Mora"
            "CANCELADO" -> "Cancelado"
            else -> estado
        }
    }

    fun obtenerColorEstado(): Long {
        return when (estado.uppercase()) {
            "ACTIVO" -> 0xFF4CAF50  // Verde
            "FINALIZADO" -> 0xFF2196F3  // Azul
            "MORA" -> 0xFFF44336  // Rojo
            "CANCELADO" -> 0xFF9E9E9E  // Gris
            else -> 0xFF9E9E9E  // Gris por defecto
        }
    }

    fun calcularProgreso(): Float {
        return if (montoPrincipal > 0) {
            ((montoPrincipal - saldoPendiente) / montoPrincipal).toFloat()
        } else {
            0f
        }
    }

    fun esPagadoCompletamente(): Boolean {
        return saldoPendiente <= 0.0
    }

    fun tieneMora(): Boolean {
        return estado.uppercase() == "MORA"
    }

    companion object {
        fun crearPrestamoEjemplo(): Prestamo {
            return Prestamo(
                clienteId = 1,
                montoPrincipal = 1000.0,
                tasaInteres = 12.0,
                numeroCuotas = 12,
                frecuenciaPago = "MENSUAL",
                tipoAmortizacion = "FRANCES",
                fechaInicio = "2024-01-15",
                fechaPrimerPago = "2024-02-15",
                saldoPendiente = 750.0,
                montoTotalPagar = 1120.0,
                totalIntereses = 120.0,
                notas = "Préstamo personal para gastos médicos",
                fechaCreacion = "2024-01-15"
            )
        }
    }
}
