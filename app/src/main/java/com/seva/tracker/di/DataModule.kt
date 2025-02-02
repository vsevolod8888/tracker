package com.seva.tracker.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import com.seva.tracker.data.room.Database
import com.seva.tracker.internetconnection.MyConnectivityManager
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
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "data"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
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