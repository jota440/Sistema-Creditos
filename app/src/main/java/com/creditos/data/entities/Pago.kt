//Pago.kt
package com.creditos.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "pr_pagos",
    indices = [
        Index(value = ["prestamo_id"]),
        Index(value = ["cuota_id"]),
        Index(value = ["fecha_pago"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Prestamo::class,
            parentColumns = ["id"],
            childColumns = ["prestamo_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Cuota::class,
            parentColumns = ["id"],
            childColumns = ["cuota_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Pago(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "prestamo_id")
    val prestamoId: Int,
    @ColumnInfo(name = "cuota_id")
    val cuotaId: Int? = null,
    val monto: Double,
    @ColumnInfo(name = "fecha_pago")
    val fechaPago: String,
    @ColumnInfo(name = "metodo_pago")
    val metodoPago: String,
    val referencia: String? = null,
    val banco: String? = null,
    val concepto: String = "CUOTA",
    @ColumnInfo(name = "aplicado_a")
    val aplicadoA: String = "CUOTA",
    @ColumnInfo(name = "usuario_registro")
    val usuario_registro: String = "SISTEMA",
    val notas: String? = null,
    @ColumnInfo(name = "fecha_pago_real")
    val fechaPagoReal: String = Date().toString(),

)