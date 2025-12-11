//CsvDataLoader.kt
package com.creditos.data.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.opencsv.CSVReaderBuilder
import java.io.InputStreamReader

object CsvDataLoader {

    fun cargarDatosIniciales(context: Context, db: SupportSQLiteDatabase) {
        android.util.Log.d("DATABASE", "📂 Iniciando carga de datos desde CSV...")
        val startTime = System.currentTimeMillis()

        try {
            // 1. Países
            cargarPaises(context, db)

            // 2. Tipos de documento
            cargarTiposDocumento(context, db)

            // 3. Comunidades (necesario antes de provincias)
            cargarComunidades(context, db)

            // 4. Provincias (necesita comunidades cargadas)
            cargarProvincias(context, db)

            // 5. Códigos postales (✅ OPTIMIZADO con batch insert)
            cargarCodigosPostalesOptimizado(context, db)

            val totalTime = System.currentTimeMillis() - startTime
            android.util.Log.d("DATABASE", "✅ Todos los datos CSV cargados en ${totalTime}ms")

        } catch (e: Exception) {
            android.util.Log.e("DATABASE", "❌ Error cargando datos CSV: ${e.message}", e)
            throw e
        }
    }

    private fun cargarPaises(context: Context, db: SupportSQLiteDatabase) {
        android.util.Log.d("DATABASE", "🌍 Cargando países...")
        var contador = 0

        context.assets.open("data/cl_paises.csv").use { inputStream ->
            val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                .withCSVParser(
                    com.opencsv.CSVParserBuilder()
                        .withSeparator(';')
                        .build()
                )
                .withSkipLines(1)
                .build()

            reader.use {
                while (true) {
                    val campos = reader.readNext() ?: break
                    if (campos.size >= 3) {
                        val orden: String = campos[0].trim()
                        val codigo: String = campos[1].trim()
                        val nombre: String = campos[2].trim()

                        db.execSQL(
                            "INSERT OR IGNORE INTO cl_paises (orden, codigo, nombre) VALUES (?, ?, ?)",
                            arrayOf<Any>(orden, codigo, nombre)
                        )
                        contador++
                    }
                }
            }
        }
        android.util.Log.d("DATABASE", "  ✓ $contador países cargados")
    }

    private fun cargarTiposDocumento(context: Context, db: SupportSQLiteDatabase) {
        android.util.Log.d("DATABASE", "📄 Cargando tipos de documento...")
        var contador = 0

        context.assets.open("data/cl_tipos_documento.csv").use { inputStream ->
            val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                .withCSVParser(
                    com.opencsv.CSVParserBuilder()
                        .withSeparator(';')
                        .build()
                )
                .withSkipLines(1)
                .build()

            reader.use {
                while (true) {
                    val campos = reader.readNext() ?: break
                    if (campos.size >= 4) {
                        val codigo: String = campos[0].trim()
                        val descripcion: String = campos[1].trim()
                        val pais: String = campos[2].trim()
                        val requiereValidacion: Int = campos[3].trim().toIntOrNull() ?: 0

                        db.execSQL(
                            "INSERT OR IGNORE INTO cl_tipos_documento (codigo, descripcion, pais, requiere_validacion) VALUES (?, ?, ?, ?)",
                            arrayOf<Any>(codigo, descripcion, pais, requiereValidacion)
                        )
                        contador++
                    }
                }
            }
        }
        android.util.Log.d("DATABASE", "  ✓ $contador tipos de documento cargados")
    }

    private fun cargarComunidades(context: Context, db: SupportSQLiteDatabase) {
        android.util.Log.d("DATABASE", "🏛️ Cargando comunidades...")
        var contador = 0

        context.assets.open("data/cl_comunidades.csv").use { inputStream ->
            val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                .withCSVParser(
                    com.opencsv.CSVParserBuilder()
                        .withSeparator(';')
                        .build()
                )
                .withSkipLines(1)
                .build()

            reader.use {
                while (true) {
                    val campos = reader.readNext() ?: break
                    if (campos.isNotEmpty()) {
                        val comunidad: String = campos[0].trim()

                        db.execSQL(
                            "INSERT OR IGNORE INTO cl_comunidades (comunidad) VALUES (?)",
                            arrayOf<Any>(comunidad)
                        )
                        contador++
                    }
                }
            }
        }
        android.util.Log.d("DATABASE", "  ✓ $contador comunidades cargadas")
    }

    private fun cargarProvincias(context: Context, db: SupportSQLiteDatabase) {
        android.util.Log.d("DATABASE", "🗺️ Cargando provincias...")
        var contador = 0

        context.assets.open("data/cl_provincias.csv").use { inputStream ->
            val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                .withCSVParser(
                    com.opencsv.CSVParserBuilder()
                        .withSeparator(';')
                        .build()
                )
                .withSkipLines(1)
                .build()

            reader.use {
                while (true) {
                    val campos = reader.readNext() ?: break
                    if (campos.size >= 3) {
                        val idComunidad: Int = campos[0].trim().toIntOrNull() ?: 0
                        val idCodigoPostal: String = campos[1].trim()
                        val provincia: String = campos[2].trim()

                        db.execSQL(
                            "INSERT OR IGNORE INTO cl_provincias (id_comunidad, id_codigo_postal, provincia) VALUES (?, ?, ?)",
                            arrayOf<Any>(idComunidad, idCodigoPostal, provincia)
                        )
                        contador++
                    }
                }
            }
        }
        android.util.Log.d("DATABASE", "  ✓ $contador provincias cargadas")
    }

    /**
     * 🚀 VERSIÓN OPTIMIZADA: Códigos postales con batch insert y transacción
     * Mejora de ~30 segundos a ~2-3 segundos
     */
    private fun cargarCodigosPostalesOptimizado(context: Context, db: SupportSQLiteDatabase) {
        android.util.Log.d("DATABASE", "📮 Cargando códigos postales (optimizado)...")
        val startTime = System.currentTimeMillis()
        var contador = 0

        // ✅ USAR TRANSACCIÓN para agrupar todas las inserciones
        db.beginTransaction()
        try {
            context.assets.open("data/cl_codigo_postales.csv").use { inputStream ->
                val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                    .withCSVParser(
                        com.opencsv.CSVParserBuilder()
                            .withSeparator(';')
                            .build()
                    )
                    .withSkipLines(1)
                    .build()

                reader.use {
                    val batchSize = 500 // Insertar en lotes de 500
                    var batch = 0

                    while (true) {
                        val campos = reader.readNext() ?: break
                        if (campos.size >= 2) {
                            val codigoPostal: String = campos[0].trim()
                            val ciudad: String = campos[1].trim()

                            db.execSQL(
                                "INSERT OR IGNORE INTO cl_codigo_postales (codigo_postal, ciudad) VALUES (?, ?)",
                                arrayOf<Any>(codigoPostal, ciudad)
                            )
                            contador++

                            // Commit cada N registros para mostrar progreso
                            if (contador % batchSize == 0) {
                                batch++
                                android.util.Log.d("DATABASE", "    ⏳ Procesados $contador CPs...")
                            }
                        }
                    }
                }
            }

            // ✅ Confirmar transacción
            db.setTransactionSuccessful()

            val totalTime = System.currentTimeMillis() - startTime
            android.util.Log.d("DATABASE", "  ✓ $contador códigos postales cargados en ${totalTime}ms")

        } catch (e: Exception) {
            android.util.Log.e("DATABASE", "❌ Error en batch insert: ${e.message}", e)
            throw e
        } finally {
            db.endTransaction()
        }
    }

    /**
     * 🛠️ VERSIÓN ALTERNATIVA: Con prepared statement (AÚN MÁS RÁPIDO)
     * Usar esta si la versión con transacción sigue siendo lenta
     */
    private fun cargarCodigosPostalesConPreparedStatement(context: Context, db: SupportSQLiteDatabase) {
        android.util.Log.d("DATABASE", "📮 Cargando códigos postales (prepared statement)...")
        val startTime = System.currentTimeMillis()
        var contador = 0

        db.beginTransaction()
        try {
            val stmt = db.compileStatement(
                "INSERT OR IGNORE INTO cl_codigo_postales (codigo_postal, ciudad) VALUES (?, ?)"
            )

            context.assets.open("data/cl_codigo_postales.csv").use { inputStream ->
                val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                    .withCSVParser(
                        com.opencsv.CSVParserBuilder()
                            .withSeparator(';')
                            .build()
                    )
                    .withSkipLines(1)
                    .build()

                reader.use {
                    while (true) {
                        val campos = reader.readNext() ?: break
                        if (campos.size >= 2) {
                            stmt.bindString(1, campos[0].trim())
                            stmt.bindString(2, campos[1].trim())
                            stmt.executeInsert()
                            contador++

                            if (contador % 1000 == 0) {
                                android.util.Log.d("DATABASE", "    ⏳ Procesados $contador CPs...")
                            }
                        }
                    }
                }
            }

            db.setTransactionSuccessful()

            val totalTime = System.currentTimeMillis() - startTime
            android.util.Log.d("DATABASE", "  ✓ $contador códigos postales cargados en ${totalTime}ms")

        } finally {
            db.endTransaction()
        }
    }
}