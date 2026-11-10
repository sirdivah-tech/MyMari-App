package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AppDao
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.DailyNoteEntity
import com.example.data.local.entity.SavingsContributionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserProfileEntity

@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        SavingsContributionEntity::class,
        DailyNoteEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MyMariDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: MyMariDatabase? = null

        fun getDatabase(context: Context): MyMariDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyMariDatabase::class.java,
                    "mymari_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
