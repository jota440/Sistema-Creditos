//Direccion.kt
package com.creditos.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "cl_direcciones",
    indices = [
        Index(value = ["cliente_id"]),
        Index(value = ["cliente_id", "predeterminada"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["cliente_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Direccion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,  // CAMBIAR de Int a Long

    @ColumnInfo(name = "cliente_id")
    val clienteId: Int,

    @ColumnInfo(name = "tipo_direccion")
    val tipoDireccion: String = "PRINCIPAL",

    val calle: String,
    val numero: String? = null,
    val piso: String? = null,
    val puerta: String? = null,

    @ColumnInfo(name = "codigo_postal")
    val codigoPostal: String? = null,

    val ciudad: String,
    val provincia: String,
    val pais: String = "España",

    val predeterminada: Boolean = false,

    val notas: String? = null,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String
) {

    fun obtenerDireccionCompleta(): String {
        return buildString {
            append(calle)
            numero?.let { append(" Nº $it") }
            piso?.let { append(", Piso $it") }
            puerta?.let { append(", Puerta $it") }
            codigoPostal?.let { append(", $it") }
            append(", $ciudad")
            append(", $provincia")
            if (pais != "España") append(", $pais")
        }
    }

    fun obtenerTipoDireccionTexto(): String {
        return when (tipoDireccion.uppercase()) {
            "PRINCIPAL" -> "Principal"
            "TRABAJO" -> "Trabajo"
            "ALTERNATIVA" -> "Alternativa"
            else -> tipoDireccion
        }
    }

    companion object {
        fun crearDireccionEjemplo(clienteId: Int): Direccion {
            return Direccion(
                clienteId = clienteId,
                tipoDireccion = "PRINCIPAL",
                calle = "Calle Ejemplo",
                numero = "123",
                piso = "2",
                puerta = "A",
                codigoPostal = "28001",
                ciudad = "Madrid",
                provincia = "Madrid",
                predeterminada = true,
                notas = "Cerca del metro",
                fechaCreacion = "2024-01-15"
            )
        }
    }
}