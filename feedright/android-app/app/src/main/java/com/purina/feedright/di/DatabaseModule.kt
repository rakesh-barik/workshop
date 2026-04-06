package com.purina.feedright.di

import android.content.Context
import androidx.room.Room
import com.purina.feedright.data.local.FeedRightDatabase
import com.purina.feedright.data.local.dao.FarmDao
import com.purina.feedright.data.local.dao.ProductDao
import com.purina.feedright.data.local.dao.VisitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Room database and DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FeedRightDatabase {
        return Room.databaseBuilder(
            context,
            FeedRightDatabase::class.java,
            FeedRightDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideVisitDao(database: FeedRightDatabase): VisitDao {
        return database.visitDao()
    }

    @Provides
    @Singleton
    fun provideFarmDao(database: FeedRightDatabase): FarmDao {
        return database.farmDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: FeedRightDatabase): ProductDao {
        return database.productDao()
    }
}
