package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pr_cuotas",
    indices = [
        Index(value = ["prestamo_id"]),
        Index(value = ["fecha_vencimiento"]),
        Index(value = ["estado"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Prestamo::class,
            parentColumns = ["id"],
            childColumns = ["prestamo_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Cuota(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val prestamo_id: Int,
    val numero_cuota: Int,
    val fecha_vencimiento: String,
    val monto_capital: Double,
    val monto_interes: Double,
    val monto_total: Double,
    val saldo_restante: Double,
    val estado: String = "PENDIENTE",
    val fecha_pago: String? = null,
    val monto_pagado: Double = 0.0,
    val dias_mora: Int = 0,
    val monto_mora: Double = 0.0
)