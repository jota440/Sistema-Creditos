package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cl_tipos_documento")
data class TipoDocumento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val codigo: String,
    val descripcion: String,
    val pais: String = "ES",
    val requiereValidacion: Boolean = true,
    val activo: Boolean = true
)