//TipoDocumento.kt
package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ColumnInfo

@Entity(tableName = "cl_tipos_documento",
    indices = [
        Index(value = ["codigo"], unique = true)])
data class TipoDocumento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String,
    val descripcion: String,
    val pais: String,
    @ColumnInfo(name = "requiere_validacion") val requiereValidacion: Int = 0
)