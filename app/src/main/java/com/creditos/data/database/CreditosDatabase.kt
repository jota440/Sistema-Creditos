//CreditosDatabase.kt
package com.creditos.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import androidx.room.TypeConverters

import com.creditos.data.entities.*
import com.creditos.data.dao.*
import com.creditos.BuildConfig
import java.io.FileOutputStream

@Database(
    entities = [
        Cliente::class,
        TipoDocumento::class,
        Direccion::class,
        Prestamo::class,
        Cuota::class,
        Pago::class,
        Pais::class,
        Comunidad::class,
        Provincia::class,
        CodigoPostal::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CreditosDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao
    abstract fun tipoDocumentoDao(): TipoDocumentoDao
    abstract fun DireccionDao(): DireccionDao
    abstract fun prestamoDao(): PrestamoDao
    abstract fun cuotaDao(): CuotaDao
    abstract fun pagoDao(): PagoDao
    abstract fun paisDao(): PaisDao
    abstract fun comunidadDao(): ComunidadDao
    abstract fun provinciaDao(): ProvinciaDao
    abstract fun codigoPostalDao(): CodigoPostalDao

    companion object {
        @Volatile
        private var INSTANCE: CreditosDatabase? = null

        private const val DB_NAME = "creditos.db"

        // 📦 Nombre de la BD pre-poblada en assets (para PRODUCCIÓN)
        private const val DB_PREPOPULATED_NAME = "databases/creditos_prepopulated.db"

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                android.util.Log.d("DATABASE", "🔄 Ejecutando migración 2 → 3")

                // Crear tabla de países
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS cl_paises (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        orden INTEGER NOT NULL,
                        codigo TEXT NOT NULL,
                        nombre TEXT NOT NULL
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_cl_paises_codigo ON cl_paises(codigo)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cl_paises_orden ON cl_paises(orden)")

                // Crear tabla de comunidades
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS cl_comunidades (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        comunidad TEXT NOT NULL
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_cl_comunidades_comunidad ON cl_comunidades(comunidad)")

                // Crear tabla de provincias
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS cl_provincias (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        id_comunidad INTEGER NOT NULL,
                        id_codigo_postal TEXT NOT NULL,
                        provincia TEXT NOT NULL,
                        FOREIGN KEY(id_comunidad) REFERENCES cl_comunidades(id) ON DELETE RESTRICT
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cl_provincias_id_comunidad ON cl_provincias(id_comunidad)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_cl_provincias_id_codigo_postal ON cl_provincias(id_codigo_postal)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cl_provincias_provincia ON cl_provincias(provincia)")

                // Crear tabla de códigos postales
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS cl_codigo_postales (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        codigo_postal TEXT NOT NULL,
                        ciudad TEXT NOT NULL
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_cl_codigo_postales_codigo_postal ON cl_codigo_postales(codigo_postal)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cl_codigo_postales_ciudad ON cl_codigo_postales(ciudad)")

                android.util.Log.d("DATABASE", "✅ Tablas de ubicación creadas - Migración completada")
            }
        }

        /**
         * 🚀 Copiar BD pre-poblada desde assets
         * Solo para PRODUCCIÓN o si existe el archivo
         */
        private fun copyPrepopulatedDatabaseFromAssets(context: Context): Boolean {
            try {
                val dbPath = context.getDatabasePath(DB_NAME)

                // Si ya existe, no copiar
                if (dbPath.exists()) {
                    android.util.Log.d("DATABASE", "⏭️ BD ya existe, saltando copia")
                    return false
                }

                // Verificar si existe el archivo pre-poblado en assets
                val assetManager = context.assets
                val assetFiles = try {
                    assetManager.list("databases") ?: emptyArray()
                } catch (e: Exception) {
                    android.util.Log.e("DATABASE", "! assetManager.list: ${e.message}")
                    emptyArray()
                }

                if (!assetFiles.contains("creditos_prepopulated.db")) {
                    android.util.Log.d("DATABASE", "ℹ️ No hay BD pre-poblada en assets")
                    return false
                }

                android.util.Log.d("DATABASE", "📦 Copiando BD pre-poblada desde assets...")

                // Crear directorio si no existe
                dbPath.parentFile?.mkdirs()

                // Copiar archivo
                assetManager.open(DB_PREPOPULATED_NAME).use { input ->
                    FileOutputStream(dbPath).use { output ->
                        input.copyTo(output)
                    }
                }

                val sizeMB = dbPath.length() / (1024.0 * 1024.0)
                android.util.Log.d("DATABASE", "✅ BD pre-poblada copiada (${String.format("%.2f", sizeMB)}MB)")
                return true
            } catch (e: Exception) {
                android.util.Log.w("DATABASE", "⚠️ No se pudo copiar BD pre-poblada: ${e.message}")
                return false
            }
        }

        fun getInstance(context: Context): CreditosDatabase {
            android.util.Log.d("DATABASE", "🔍 getInstance() llamado")

            return INSTANCE ?: synchronized(this) {
                // 🎯 ESTRATEGIA AUTOMÁTICA:
                // 1. PRODUCCIÓN (RELEASE): Intentar copiar BD pre-poblada
                // 2. DESARROLLO (DEBUG): Usar CSV para flexibilidad

                if (!BuildConfig.DEBUG) {
                    // 📦 MODO PRODUCCIÓN: Intentar usar BD pre-poblada
                    android.util.Log.d("DATABASE", "📦 Modo PRODUCCIÓN detectado")
                    val copied = copyPrepopulatedDatabaseFromAssets(context)
                    if (copied) {
                        android.util.Log.d("DATABASE", "✅ Usando BD pre-poblada")
                    } else {
                        android.util.Log.d("DATABASE", "⚠️ BD pre-poblada no disponible, se usará CSV")
                    }
                } else {
                    // 🔧 MODO DESARROLLO: Usar CSV (permite backups)
                    android.util.Log.d("DATABASE", "🔧 Modo DESARROLLO detectado - usando CSV")
                }

                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    CreditosDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            android.util.Log.d("DATABASE", "✅ Base de datos CREADA")
                            if (BuildConfig.DEBUG) {
                                android.util.Log.d("DATABASE", "ℹ️ Los datos CSV se cargarán desde Application")
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            android.util.Log.d("DATABASE", "📂 Base de datos ABIERTA")
                        }
                    })
                    .addMigrations(MIGRATION_2_3)

                // Solo en DEBUG permite destruir/recrear
                if (BuildConfig.DEBUG) {
                    builder.fallbackToDestructiveMigration(dropAllTables = true)
                }

                val instance = builder.build()

                try {
                    val sqlDb = instance.openHelper.writableDatabase
                    android.util.Log.d("DATABASE", "📁 Ruta BD: ${sqlDb.path}")
                } catch (e: Exception) {
                    android.util.Log.e("DATABASE", "❌ Error obteniendo ruta: $e")
                }

                INSTANCE = instance
                android.util.Log.d("DATABASE", "💾 Instancia guardada")
                instance
            }
        }

        /**
         * 🛠️ SOLO DESARROLLO: Resetear BD para forzar recarga
         */
        fun resetDatabaseForDevelopment(context: Context) {
            if (!BuildConfig.DEBUG) {
                android.util.Log.w("DATABASE", "⚠️ resetDatabase solo funciona en DEBUG")
                return
            }

            synchronized(this) {
                try {
                    INSTANCE?.close()
                    INSTANCE = null

                    val dbPath = context.getDatabasePath(DB_NAME)
                    if (dbPath.exists()) {
                        val deleted = dbPath.delete()
                        if (deleted) {
                            android.util.Log.d("DATABASE", "🗑️ BD eliminada para forzar recarga")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DATABASE", "❌ Error reseteando BD: ${e.message}", e)
                }
            }
        }
    }
}