//Cliente.kt
package com.creditos.data.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(    tableName = "cl_clientes",
    indices = [
        Index(value = ["tipo_documento_id", "numero_documento"], unique = true),
        Index(value = ["nombre", "apellido", "numero_documento", "telefono_principal", "email", "activo"])
    ]
)
data class Cliente(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val apellido: String,
    @ColumnInfo(name = "tipo_documento_id")
    val tipoDocumentoId: Int,
    @ColumnInfo(name = "numero_documento")
    val numeroDocumento: String,
    @ColumnInfo(name = "telefono_principal")
    val telefonoPrincipal: String,
    @ColumnInfo(name = "telefono_secundario")
    val telefonoSecundario: String? = null,
    val email: String? = null,
    @ColumnInfo(name = "fecha_nacimiento")
    val fechaNacimiento: String? = null,
    val ocupacion: String? = null,
    val notas: String? = null,
    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: String = Date().toString(),
    val activo: Boolean = true
)