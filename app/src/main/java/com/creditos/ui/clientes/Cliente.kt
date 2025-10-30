package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cl_clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val apellido: String,
    val tipoDocumentoId: Int,
    val numeroDocumento: String,
    val telefonoPrincipal: String,
    val telefonoSecundario: String? = null,
    val email: String? = null,
    val fechaNacimiento: String? = null,
    val ocupacion: String? = null,
    val notas: String? = null,
    val fechaRegistro: String,
    val activo: Boolean = true
)