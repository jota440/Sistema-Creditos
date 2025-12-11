//Pais.kt
package com.creditos.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cl_paises",
    indices = [
        Index(value = ["codigo"], unique = true),
        Index(value = ["orden"])
    ]
)
data class Pais(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val orden: Int,

    @ColumnInfo(name = "codigo")
    val codigo: String,  // ISO 3166-1 alpha-3 (ESP, FRA, USA, etc.)

    @ColumnInfo(name = "nombre")
    val nombre: String
) {
    companion object {
        fun paisesIniciales(): List<Pais> {
            return listOf(
                Pais(orden = 1, codigo = "ESP", nombre = "España"),
                Pais(orden = 2, codigo = "FRA", nombre = "Francia"),
                Pais(orden = 3, codigo = "PRT", nombre = "Portugal"),
                Pais(orden = 4, codigo = "ITA", nombre = "Italia"),
                Pais(orden = 5, codigo = "DEU", nombre = "Alemania"),
                Pais(orden = 6, codigo = "GBR", nombre = "Reino Unido"),
                Pais(orden = 7, codigo = "USA", nombre = "Estados Unidos"),
                Pais(orden = 8, codigo = "MEX", nombre = "México"),
                Pais(orden = 9, codigo = "ARG", nombre = "Argentina"),
                Pais(orden = 10, codigo = "COL", nombre = "Colombia")
            )
        }
    }
}