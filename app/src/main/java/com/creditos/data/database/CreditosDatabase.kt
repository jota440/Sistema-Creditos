package com.creditos.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

import com.creditos.data.entities.Cliente
import com.creditos.data.entities.TipoDocumento
import com.creditos.data.entities.Prestamo
import com.creditos.data.entities.Cuota
import com.creditos.data.entities.Pago

import com.creditos.data.dao.ClienteDao
import com.creditos.data.dao.TipoDocumentoDao
import com.creditos.data.dao.PrestamoDao
import com.creditos.data.dao.CuotaDao
import com.creditos.data.dao.PagoDao

@Database(
    entities = [
        Cliente::class,
        TipoDocumento::class,
        Prestamo::class,
        Cuota::class,
        Pago::class  // ← AGREGAR ESTO
    ],
    version = 2,  // ← INCREMENTAR VERSIÓN
    exportSchema = false
)
abstract class CreditosDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao
    abstract fun tipoDocumentoDao(): TipoDocumentoDao
    abstract fun prestamoDao(): PrestamoDao
    abstract fun cuotaDao(): CuotaDao
    abstract fun pagoDao(): PagoDao  // ← AGREGAR ESTO

    companion object {
        // ... código existente

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crear tabla de cuotas
                database.execSQL("""
                    CREATE TABLE pr_cuotas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        prestamo_id INTEGER NOT NULL,
                        numero_cuota INTEGER NOT NULL,
                        fecha_vencimiento TEXT NOT NULL,
                        monto_capital REAL NOT NULL,
                        monto_interes REAL NOT NULL,
                        monto_total REAL NOT NULL,
                        saldo_restante REAL NOT NULL,
                        estado TEXT DEFAULT 'PENDIENTE',
                        fecha_pago TEXT,
                        monto_pagado REAL DEFAULT 0,
                        dias_mora INTEGER DEFAULT 0,
                        monto_mora REAL DEFAULT 0,
                        FOREIGN KEY (prestamo_id) REFERENCES pr_prestamos(id) ON DELETE CASCADE,
                        UNIQUE(prestamo_id, numero_cuota)
                    )
                """)

                // Crear tabla de pagos
                database.execSQL("""
                    CREATE TABLE pr_pagos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        prestamo_id INTEGER NOT NULL,
                        cuota_id INTEGER,
                        monto REAL NOT NULL,
                        fecha_pago TEXT NOT NULL,
                        metodo_pago TEXT NOT NULL,
                        referencia TEXT,
                        banco TEXT,
                        concepto TEXT DEFAULT 'CUOTA',
                        aplicado_a TEXT DEFAULT 'CUOTA',
                        usuario_registro TEXT DEFAULT 'SISTEMA',
                        notas TEXT,
                        FOREIGN KEY (prestamo_id) REFERENCES pr_prestamos(id) ON DELETE CASCADE,
                        FOREIGN KEY (cuota_id) REFERENCES pr_cuotas(id) ON DELETE SET NULL
                    )
                """)

                // Crear índices
                database.execSQL("CREATE INDEX idx_pr_cuotas_prestamo ON pr_cuotas(prestamo_id)")
                database.execSQL("CREATE INDEX idx_pr_cuotas_fecha ON pr_cuotas(fecha_vencimiento)")
                database.execSQL("CREATE INDEX idx_pr_cuotas_estado ON pr_cuotas(estado)")
                database.execSQL("CREATE INDEX idx_pr_pagos_prestamo ON pr_pagos(prestamo_id)")
                database.execSQL("CREATE INDEX idx_pr_pagos_fecha ON pr_pagos(fecha_pago)")
            }
        }

        fun getInstance(context: Context): CreditosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CreditosDatabase::class.java,
                    "creditos_database"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        insertTiposDocumentoIniciales(db)
                    }
                })
                    .addMigrations(MIGRATION_1_2)  // ← AGREGAR MIGRACIÓN
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}