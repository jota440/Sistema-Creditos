package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pr_prestamos",
    indices = [Index(value = ["clienteId"]), Index(value = ["estado"])],
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Prestamo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val clienteId: Int,
    val montoPrincipal: Double,
    val tasaInteres: Double,
    val numeroCuotas: Int,
    val frecuenciaPago: String,
    val tipoAmortizacion: String = "FRANCES",
    val fechaInicio: String,
    val fechaPrimerPago: String,
    val estado: String = "ACTIVO",
    val saldoPendiente: Double,
    val montoTotalPagar: Double,
    val totalIntereses: Double,
    val notas: String? = null,
    val fechaCreacion: String,
    val fechaFinalizacion: String? = null
)