//CreditosDatabase.kt
package com.creditos.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase

import com.creditos.data.entities.Cliente
import com.creditos.data.entities.TipoDocumento
import com.creditos.data.entities.Cuota
import com.creditos.data.entities.Pago
import com.creditos.data.entities.Prestamo
import com.creditos.data.dao.ClienteDao
import com.creditos.data.dao.TipoDocumentoDao
import com.creditos.data.dao.PrestamoDao
import com.creditos.data.dao.CuotaDao
import com.creditos.data.dao.PagoDao
import com.creditos.BuildConfig
import com.creditos.data.entities.Direccion
import com.creditos.data.dao.DireccionDao

@Database(
    entities = [
        Cliente::class,
        TipoDocumento::class,
        Direccion::class,
        Prestamo::class,
        Cuota::class,
        Pago::class
    ],
    version = 2,
    exportSchema = false
)

abstract class CreditosDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao
    abstract fun tipoDocumentoDao(): TipoDocumentoDao
    abstract fun DireccionDao(): DireccionDao
    abstract fun prestamoDao(): PrestamoDao
    abstract fun cuotaDao(): CuotaDao
    abstract fun pagoDao(): PagoDao

    companion object {
        @Volatile
        private var INSTANCE: CreditosDatabase? = null

        fun getInstance(context: Context): CreditosDatabase {
            android.util.Log.d("DATABASE", "🔍 getInstance() llamado")

            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    CreditosDatabase::class.java,
                    "creditos.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            android.util.Log.d("DATABASE", "✅ Base de datos CREADA por primera vez")
                            insertTiposDocumentoIniciales(db)
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            android.util.Log.d("DATABASE", "📂 Base de datos ABIERTA")
                        }
                    })

                // 🔒 Solo en DEBUG permite destruir/recrear la BD al cambiar el esquema.
                if (BuildConfig.DEBUG) {
                    builder.fallbackToDestructiveMigration(true)
                }

                val instance = builder.build()

                // 👇 Fuerza apertura y muestra ruta física (solo para depuración)
                try {
                    val sqlDb = instance.openHelper.writableDatabase
                    android.util.Log.d("DATABASE", "📍 Ruta: ${sqlDb.path}")
                } catch (e: Exception) {
                    android.util.Log.d("DATABASE", "Error asignando ruta: $e")
                }

                INSTANCE = instance
                android.util.Log.d("DATABASE", "💾 Instancia guardada")
                instance
            }
        }

        private fun insertTiposDocumentoIniciales(db: SupportSQLiteDatabase) {
            android.util.Log.d("DATABASE", "📝 Insertando tipos de documento iniciales...")

            // ✅ Ahora que tu entidad tiene 'requiere_validacion', los INSERT pueden incluirla.
            val tipos = listOf(
                "('DNI', 'DNI', 'ES', 0)",
                "('NIE', 'NIE', 'ES', 0)",
                "('PASAPORTE', 'Pasaporte', 'ES', 0)",
                "('CIF', 'CIF', 'ES', 0)"
            )

            tipos.forEach { valores ->
                db.execSQL(
                    "INSERT INTO cl_tipos_documento (codigo, descripcion, pais, requiere_validacion) VALUES $valores"
                )
            }

            android.util.Log.d("DATABASE", "✅ Tipos de documento insertados")
        }
    }
}
