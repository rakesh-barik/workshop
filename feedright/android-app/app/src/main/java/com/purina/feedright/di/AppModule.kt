package com.purina.feedright.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.UUID
import javax.inject.Singleton

/**
 * Hilt module providing application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("feedright_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDeviceId(
        sharedPreferences: SharedPreferences
    ): String {
        return sharedPreferences.getString("device_id", null) ?: run {
            val newId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString("device_id", newId).apply()
            newId
        }
    }
}
