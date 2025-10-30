package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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

    val prestamo_id: Int,
    val cuota_id: Int? = null,
    val monto: Double,
    val fecha_pago: String,
    val metodo_pago: String,
    val referencia: String? = null,
    val banco: String? = null,
    val concepto: String = "CUOTA",
    val aplicado_a: String = "CUOTA",
    val usuario_registro: String = "SISTEMA",
    val notas: String? = null
)