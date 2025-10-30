package com.creditos.di

import android.content.Context
import com.creditos.data.database.CreditosDatabase
import com.creditos.data.dao.ClienteDao
import com.creditos.data.dao.TipoDocumentoDao
import com.creditos.data.dao.PagoDao
import com.creditos.data.dao.CuotaDao
import com.creditos.data.repository.ClienteRepository
import com.creditos.data.repository.TipoDocumentoRepository
import com.creditos.data.repository.PagoRepository
import com.creditos.data.repository.CuotaRepository
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
        return CreditosDatabase.getInstance(context)
    }

    @Provides
    fun provideClienteDao(database: CreditosDatabase): ClienteDao {
        return database.clienteDao()
    }

    @Provides
    fun provideTipoDocumentoDao(database: CreditosDatabase): TipoDocumentoDao {
        return database.tipoDocumentoDao()
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
}