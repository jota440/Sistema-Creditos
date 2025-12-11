//CodigoPostal.kt
package com.creditos.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cl_codigo_postales",
    indices = [
        Index(value = ["codigo_postal"], unique = true),
        Index(value = ["ciudad"])
    ]
)
data class CodigoPostal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "codigo_postal")
    val codigoPostal: String,  // 5 dígitos (28001, 08001, etc.)

    @ColumnInfo(name = "ciudad")
    val ciudad: String
) {
    // Helper para obtener código de provincia (primeros 2 dígitos)
    fun getCodigoProvincia(): String = codigoPostal.take(2)

    companion object {
        // Algunos códigos postales de ejemplo por provincia
        // En producción, esto vendría de una base de datos externa o CSV
        fun codigosPostalesEjemplo(): List<CodigoPostal> {
            return listOf(
                // Madrid (28)
                CodigoPostal(codigoPostal = "28001", ciudad = "Madrid"),
                CodigoPostal(codigoPostal = "28002", ciudad = "Madrid"),
                CodigoPostal(codigoPostal = "28003", ciudad = "Madrid"),
                CodigoPostal(codigoPostal = "28013", ciudad = "Madrid"),
                CodigoPostal(codigoPostal = "28028", ciudad = "Madrid"),
                CodigoPostal(codigoPostal = "28670", ciudad = "Villaviciosa de Odón"),

                // Barcelona (08)
                CodigoPostal(codigoPostal = "08001", ciudad = "Barcelona"),
                CodigoPostal(codigoPostal = "08002", ciudad = "Barcelona"),
                CodigoPostal(codigoPostal = "08003", ciudad = "Barcelona"),
                CodigoPostal(codigoPostal = "08015", ciudad = "Barcelona"),
                CodigoPostal(codigoPostal = "08940", ciudad = "Cornellà de Llobregat"),

                // Valencia (46)
                CodigoPostal(codigoPostal = "46001", ciudad = "Valencia"),
                CodigoPostal(codigoPostal = "46002", ciudad = "Valencia"),
                CodigoPostal(codigoPostal = "46003", ciudad = "Valencia"),
                CodigoPostal(codigoPostal = "46015", ciudad = "Valencia"),

                // Sevilla (41)
                CodigoPostal(codigoPostal = "41001", ciudad = "Sevilla"),
                CodigoPostal(codigoPostal = "41002", ciudad = "Sevilla"),
                CodigoPostal(codigoPostal = "41003", ciudad = "Sevilla"),
                CodigoPostal(codigoPostal = "41013", ciudad = "Sevilla"),

                // Málaga (29)
                CodigoPostal(codigoPostal = "29001", ciudad = "Málaga"),
                CodigoPostal(codigoPostal = "29002", ciudad = "Málaga"),
                CodigoPostal(codigoPostal = "29003", ciudad = "Málaga"),
                CodigoPostal(codigoPostal = "29600", ciudad = "Marbella"),

                // Bilbao (48)
                CodigoPostal(codigoPostal = "48001", ciudad = "Bilbao"),
                CodigoPostal(codigoPostal = "48002", ciudad = "Bilbao"),
                CodigoPostal(codigoPostal = "48003", ciudad = "Bilbao")
            )
        }
    }
}