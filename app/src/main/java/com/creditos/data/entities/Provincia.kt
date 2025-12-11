//Provincia.kt
package com.creditos.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cl_provincias",
    indices = [
        Index(value = ["id_comunidad"]),
        Index(value = ["id_codigo_postal"], unique = true),
        Index(value = ["provincia"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Comunidad::class,
            parentColumns = ["id"],
            childColumns = ["id_comunidad"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Provincia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "id_comunidad")
    val idComunidad: Int,

    @ColumnInfo(name = "id_codigo_postal")
    val idCodigoPostal: String,  // 2 dígitos (01-52)

    @ColumnInfo(name = "provincia")
    val provincia: String
) {
    companion object {
        // Datos de las 52 provincias españolas
        fun provinciasEspana(comunidadIds: Map<String, Int>): List<Provincia> {
            return listOf(
                // Andalucía
                Provincia(idComunidad = comunidadIds["Andalucía"]!!, idCodigoPostal = "04", provincia = "Almería"),
                Provincia(idComunidad = comunidadIds["Andalucía"]!!, idCodigoPostal = "11", provincia = "Cádiz"),
                Provincia(idComunidad = comunidadIds["Andalucía"]!!, idCodigoPostal = "14", provincia = "Córdoba"),
                Provincia(idComunidad = comunidadIds["Andalucía"]!!, idCodigoPostal = "18", provincia = "Granada"),
                Provincia(idComunidad = comunidadIds["Andalucía"]!!, idCodigoPostal = "21", provincia = "Huelva"),
                Provincia(idComunidad = comunidadIds["Andalucía"]!!, idCodigoPostal = "23", provincia = "Jaén"),
                Provincia(idComunidad = comunidadIds["Andalucía"]!!, idCodigoPostal = "29", provincia = "Málaga"),
                Provincia(idComunidad = comunidadIds["Andalucía"]!!, idCodigoPostal = "41", provincia = "Sevilla"),

                // Aragón
                Provincia(idComunidad = comunidadIds["Aragón"]!!, idCodigoPostal = "22", provincia = "Huesca"),
                Provincia(idComunidad = comunidadIds["Aragón"]!!, idCodigoPostal = "44", provincia = "Teruel"),
                Provincia(idComunidad = comunidadIds["Aragón"]!!, idCodigoPostal = "50", provincia = "Zaragoza"),

                // Asturias
                Provincia(idComunidad = comunidadIds["Asturias"]!!, idCodigoPostal = "33", provincia = "Asturias"),

                // Islas Baleares
                Provincia(idComunidad = comunidadIds["Islas Baleares"]!!, idCodigoPostal = "07", provincia = "Islas Baleares"),

                // Canarias
                Provincia(idComunidad = comunidadIds["Canarias"]!!, idCodigoPostal = "35", provincia = "Las Palmas"),
                Provincia(idComunidad = comunidadIds["Canarias"]!!, idCodigoPostal = "38", provincia = "Santa Cruz de Tenerife"),

                // Cantabria
                Provincia(idComunidad = comunidadIds["Cantabria"]!!, idCodigoPostal = "39", provincia = "Cantabria"),

                // Castilla y León
                Provincia(idComunidad = comunidadIds["Castilla y León"]!!, idCodigoPostal = "05", provincia = "Ávila"),
                Provincia(idComunidad = comunidadIds["Castilla y León"]!!, idCodigoPostal = "09", provincia = "Burgos"),
                Provincia(idComunidad = comunidadIds["Castilla y León"]!!, idCodigoPostal = "24", provincia = "León"),
                Provincia(idComunidad = comunidadIds["Castilla y León"]!!, idCodigoPostal = "34", provincia = "Palencia"),
                Provincia(idComunidad = comunidadIds["Castilla y León"]!!, idCodigoPostal = "37", provincia = "Salamanca"),
                Provincia(idComunidad = comunidadIds["Castilla y León"]!!, idCodigoPostal = "40", provincia = "Segovia"),
                Provincia(idComunidad = comunidadIds["Castilla y León"]!!, idCodigoPostal = "42", provincia = "Soria"),
                Provincia(idComunidad = comunidadIds["Castilla y León"]!!, idCodigoPostal = "47", provincia = "Valladolid"),
                Provincia(idComunidad = comunidadIds["Castilla y León"]!!, idCodigoPostal = "49", provincia = "Zamora"),

                // Castilla-La Mancha
                Provincia(idComunidad = comunidadIds["Castilla-La Mancha"]!!, idCodigoPostal = "02", provincia = "Albacete"),
                Provincia(idComunidad = comunidadIds["Castilla-La Mancha"]!!, idCodigoPostal = "13", provincia = "Ciudad Real"),
                Provincia(idComunidad = comunidadIds["Castilla-La Mancha"]!!, idCodigoPostal = "16", provincia = "Cuenca"),
                Provincia(idComunidad = comunidadIds["Castilla-La Mancha"]!!, idCodigoPostal = "19", provincia = "Guadalajara"),
                Provincia(idComunidad = comunidadIds["Castilla-La Mancha"]!!, idCodigoPostal = "45", provincia = "Toledo"),

                // Cataluña
                Provincia(idComunidad = comunidadIds["Cataluña"]!!, idCodigoPostal = "08", provincia = "Barcelona"),
                Provincia(idComunidad = comunidadIds["Cataluña"]!!, idCodigoPostal = "17", provincia = "Girona"),
                Provincia(idComunidad = comunidadIds["Cataluña"]!!, idCodigoPostal = "25", provincia = "Lleida"),
                Provincia(idComunidad = comunidadIds["Cataluña"]!!, idCodigoPostal = "43", provincia = "Tarragona"),

                // Comunidad Valenciana
                Provincia(idComunidad = comunidadIds["Comunidad Valenciana"]!!, idCodigoPostal = "03", provincia = "Alicante"),
                Provincia(idComunidad = comunidadIds["Comunidad Valenciana"]!!, idCodigoPostal = "12", provincia = "Castellón"),
                Provincia(idComunidad = comunidadIds["Comunidad Valenciana"]!!, idCodigoPostal = "46", provincia = "Valencia"),

                // Extremadura
                Provincia(idComunidad = comunidadIds["Extremadura"]!!, idCodigoPostal = "06", provincia = "Badajoz"),
                Provincia(idComunidad = comunidadIds["Extremadura"]!!, idCodigoPostal = "10", provincia = "Cáceres"),

                // Galicia
                Provincia(idComunidad = comunidadIds["Galicia"]!!, idCodigoPostal = "15", provincia = "A Coruña"),
                Provincia(idComunidad = comunidadIds["Galicia"]!!, idCodigoPostal = "27", provincia = "Lugo"),
                Provincia(idComunidad = comunidadIds["Galicia"]!!, idCodigoPostal = "32", provincia = "Ourense"),
                Provincia(idComunidad = comunidadIds["Galicia"]!!, idCodigoPostal = "36", provincia = "Pontevedra"),

                // Madrid
                Provincia(idComunidad = comunidadIds["Madrid"]!!, idCodigoPostal = "28", provincia = "Madrid"),

                // Murcia
                Provincia(idComunidad = comunidadIds["Murcia"]!!, idCodigoPostal = "30", provincia = "Murcia"),

                // Navarra
                Provincia(idComunidad = comunidadIds["Navarra"]!!, idCodigoPostal = "31", provincia = "Navarra"),

                // País Vasco
                Provincia(idComunidad = comunidadIds["País Vasco"]!!, idCodigoPostal = "01", provincia = "Álava"),
                Provincia(idComunidad = comunidadIds["País Vasco"]!!, idCodigoPostal = "20", provincia = "Gipuzkoa"),
                Provincia(idComunidad = comunidadIds["País Vasco"]!!, idCodigoPostal = "48", provincia = "Bizkaia"),

                // La Rioja
                Provincia(idComunidad = comunidadIds["La Rioja"]!!, idCodigoPostal = "26", provincia = "La Rioja"),

                // Ceuta
                Provincia(idComunidad = comunidadIds["Ceuta"]!!, idCodigoPostal = "51", provincia = "Ceuta"),

                // Melilla
                Provincia(idComunidad = comunidadIds["Melilla"]!!, idCodigoPostal = "52", provincia = "Melilla")
            )
        }
    }
}