package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

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
    @ColumnInfo(name = "prestamo_id")
    val prestamoId: Int,

    @ColumnInfo(name = "numero_cuota")
    val numeroCuota: Int,

    @ColumnInfo(name = "fecha_vencimiento")
    val fechaVencimiento: String,

    @ColumnInfo(name = "monto_capital")
    val montoCapital: Double,

    @ColumnInfo(name = "monto_interes")
    val montoInteres: Double,

    @ColumnInfo(name = "monto_total")
    val montoTotal: Double,

    @ColumnInfo(name = "saldo_restante")
    val saldoRestante: Double,

    @ColumnInfo(name = "estado")
    val estado: String = "PENDIENTE", // PENDIENTE, PAGADA, VENCIDA, PARCIAL

    @ColumnInfo(name = "fecha_pago")
    val fechaPago: String? = null,

    @ColumnInfo(name = "monto_pagado")
    val montoPagado: Double = 0.0,

    @ColumnInfo(name = "dias_mora")
    val diasMora: Int = 0,

    @ColumnInfo(name = "monto_mora")
    val montoMora: Double = 0.0
)