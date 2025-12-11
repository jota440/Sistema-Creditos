//Comunidad.kt
package com.creditos.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cl_comunidades",
    indices = [Index(value = ["comunidad"], unique = true)]
)
data class Comunidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "comunidad")
    val comunidad: String
) {
    companion object {
        fun comunidadesEspana(): List<Comunidad> {
            return listOf(
                Comunidad(comunidad = "Andalucía"),
                Comunidad(comunidad = "Aragón"),
                Comunidad(comunidad = "Asturias"),
                Comunidad(comunidad = "Islas Baleares"),
                Comunidad(comunidad = "Canarias"),
                Comunidad(comunidad = "Cantabria"),
                Comunidad(comunidad = "Castilla y León"),
                Comunidad(comunidad = "Castilla-La Mancha"),
                Comunidad(comunidad = "Cataluña"),
                Comunidad(comunidad = "Comunidad Valenciana"),
                Comunidad(comunidad = "Extremadura"),
                Comunidad(comunidad = "Galicia"),
                Comunidad(comunidad = "Madrid"),
                Comunidad(comunidad = "Murcia"),
                Comunidad(comunidad = "Navarra"),
                Comunidad(comunidad = "País Vasco"),
                Comunidad(comunidad = "La Rioja"),
                Comunidad(comunidad = "Ceuta"),
                Comunidad(comunidad = "Melilla")
            )
        }
    }
}