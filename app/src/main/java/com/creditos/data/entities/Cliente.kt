//Cliente.kt
package com.creditos.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "cl_clientes",
    indices = [
        // Evita documentos duplicados para un mismo tipo de documento
        Index(value = ["tipo_documento_id", "numero_documento"], unique = true),

        // Búsquedas directas por número de documento
        Index(value = ["numero_documento"]),

        // Búsquedas por teléfono y email
        Index(value = ["telefono_principal"]),
        Index(value = ["email"]),

        // Listados y búsquedas por nombre + apellido
        Index(value = ["nombre", "apellido"]),

        // Búsquedas solo por apellido (ej: 'hernan%')
        Index(value = ["apellido"])
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
    val fechaNacimiento: Date? = null,  // Solo fecha, sin hora (manejar en UI)

    val ocupacion: String? = null,
    val notas: String? = null,

    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Date = Date(),   // Fecha y hora completas

    val activo: Boolean = true
)
