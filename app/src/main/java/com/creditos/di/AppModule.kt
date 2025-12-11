//AppModule.kt
package com.creditos.di

import android.content.Context
import com.creditos.data.database.CreditosDatabase
import com.creditos.data.dao.*
import com.creditos.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CreditosDatabase {
        android.util.Log.d("HILT", "⚡ provideDatabase() EJECUTADO")
        return CreditosDatabase.getInstance(context)
    }

    // ========== DAOs ==========

    @Provides
    fun provideClienteDao(database: CreditosDatabase): ClienteDao {
        return database.clienteDao()
    }

    @Provides
    fun provideTipoDocumentoDao(database: CreditosDatabase): TipoDocumentoDao {
        return database.tipoDocumentoDao()
    }

    @Provides
    @Singleton
    fun providePrestamoDao(database: CreditosDatabase): PrestamoDao {
        return database.prestamoDao()
    }

    @Provides
    fun providePagoDao(database: CreditosDatabase): PagoDao {
        return database.pagoDao()
    }

    @Provides
    fun provideCuotaDao(database: CreditosDatabase): CuotaDao {
        return database.cuotaDao()
    }

    @Provides
    fun provideDireccionDao(database: CreditosDatabase): DireccionDao {
        return database.DireccionDao()
    }

    // ✅ Nuevos DAOs
    @Provides
    fun providePaisDao(database: CreditosDatabase): PaisDao {
        return database.paisDao()
    }

    @Provides
    fun provideComunidadDao(database: CreditosDatabase): ComunidadDao {
        return database.comunidadDao()
    }

    @Provides
    fun provideProvinciaDao(database: CreditosDatabase): ProvinciaDao {
        return database.provinciaDao()
    }

    @Provides
    fun provideCodigoPostalDao(database: CreditosDatabase): CodigoPostalDao {
        return database.codigoPostalDao()
    }

    // ========== Repositories ==========

    @Provides
    fun provideClienteRepository(clienteDao: ClienteDao): ClienteRepository {
        return ClienteRepository(clienteDao)
    }

    @Provides
    fun provideTipoDocumentoRepository(tipoDocumentoDao: TipoDocumentoDao): TipoDocumentoRepository {
        return TipoDocumentoRepository(tipoDocumentoDao)
    }

    @Provides
    fun providePagoRepository(pagoDao: PagoDao): PagoRepository {
        return PagoRepository(pagoDao)
    }

    @Provides
    fun provideCuotaRepository(cuotaDao: CuotaDao): CuotaRepository {
        return CuotaRepository(cuotaDao)
    }

    @Provides
    fun provideDireccionRepository(direccionDao: DireccionDao): DireccionRepository {
        return DireccionRepository(direccionDao)
    }

    // ✅ CORRECTO: Con ambos parámetros
    @Provides
    fun providePrestamoRepository(
        prestamoDao: PrestamoDao,
        cuotaDao: CuotaDao
    ): PrestamoRepository {
        return PrestamoRepository(prestamoDao, cuotaDao)
    }

    // ✅ Nuevos Repositories
    @Provides
    fun providePaisRepository(paisDao: PaisDao): PaisRepository {
        return PaisRepository(paisDao)
    }

    @Provides
    fun provideComunidadRepository(comunidadDao: ComunidadDao): ComunidadRepository {
        return ComunidadRepository(comunidadDao)
    }

    @Provides
    fun provideProvinciaRepository(provinciaDao: ProvinciaDao): ProvinciaRepository {
        return ProvinciaRepository(provinciaDao)
    }

    @Provides
    fun provideCodigoPostalRepository(codigoPostalDao: CodigoPostalDao): CodigoPostalRepository {
        return CodigoPostalRepository(codigoPostalDao)
    }
}