package com.seva.tracker.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import com.seva.tracker.data.repository.Repository
import com.seva.tracker.data.repository.impl.RepositoryImpl
import com.seva.tracker.data.room.Database
import com.seva.tracker.data.room.MIGRATION_1_2
import com.seva.tracker.internetconnection.MyConnectivityManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "data"
        )
            .allowMainThreadQueries()
            //   .fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_1_2)
            .build()

    }





    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("ServiceCast")
    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): MyConnectivityManager {
        return MyConnectivityManager(context,GlobalScope)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(
        repositoryImpl: RepositoryImpl
    ): Repository
}