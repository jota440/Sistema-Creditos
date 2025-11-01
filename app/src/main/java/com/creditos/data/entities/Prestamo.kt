package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pr_prestamos",
    indices = [
        Index(value = ["cliente_id"]),
        Index(value = ["estado"])
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

    val cliente_id: Int,
    val monto_principal: Double,
    val tasa_interes: Double,
    val numero_cuotas: Int,
    val frecuencia_pago: String,
    val tipo_amortizacion: String = "FRANCES",
    val fecha_inicio: String,
    val fecha_primer_pago: String,
    val estado: String = "ACTIVO",
    val saldo_pendiente: Double,
    val monto_total_pagar: Double,
    val total_intereses: Double,
    val notas: String? = null,
    val fecha_creacion: String,
    val fecha_finalizacion: String? = null
) {

    fun obtenerFrecuenciaPagoTexto(): String {
        return when (frecuencia_pago.uppercase()) {
            "SEMANAL" -> "Semanal"
            "QUINCENAL" -> "Quincenal"
            "MENSUAL" -> "Mensual"
            "ANUAL" -> "Anual"
            else -> frecuencia_pago
        }
    }

    fun obtenerTipoAmortizacionTexto(): String {
        return when (tipo_amortizacion.uppercase()) {
            "FRANCES" -> "Francés"
            "ALEMAN" -> "Alemán"
            "AMERICANO" -> "Americano"
            else -> tipo_amortizacion
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
        return if (monto_principal > 0) {
            ((monto_principal - saldo_pendiente) / monto_principal).toFloat()
        } else {
            0f
        }
    }

    fun esPagadoCompletamente(): Boolean {
        return saldo_pendiente <= 0.0
    }

    fun tieneMora(): Boolean {
        return estado.uppercase() == "MORA"
    }

    companion object {
        fun crearPrestamoEjemplo(): Prestamo {
            return Prestamo(
                cliente_id = 1,
                monto_principal = 1000.0,
                tasa_interes = 12.0,
                numero_cuotas = 12,
                frecuencia_pago = "MENSUAL",
                tipo_amortizacion = "FRANCES",
                fecha_inicio = "2024-01-15",
                fecha_primer_pago = "2024-02-15",
                saldo_pendiente = 750.0,
                monto_total_pagar = 1120.0,
                total_intereses = 120.0,
                notas = "Préstamo personal para gastos médicos",
                fecha_creacion = "2024-01-15"
            )
        }
    }
}
