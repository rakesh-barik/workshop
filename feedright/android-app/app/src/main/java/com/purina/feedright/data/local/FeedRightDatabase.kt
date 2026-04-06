package com.purina.feedright.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.purina.feedright.data.local.dao.FarmDao
import com.purina.feedright.data.local.dao.ProductDao
import com.purina.feedright.data.local.dao.VisitDao
import com.purina.feedright.data.local.entity.FarmEntity
import com.purina.feedright.data.local.entity.ProductEntity
import com.purina.feedright.data.local.entity.VisitEntity

@Database(
    entities = [
        VisitEntity::class,
        FarmEntity::class,
        ProductEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FeedRightDatabase : RoomDatabase() {
    abstract fun visitDao(): VisitDao
    abstract fun farmDao(): FarmDao
    abstract fun productDao(): ProductDao

    companion object {
        const val DATABASE_NAME = "feedright_db"
    }
}
