package com.example.pawstagram.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PawDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var instance: PawDatabase? = null

        fun getInstance(context: Context): PawDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PawDatabase::class.java,
                    "pawstagram.db"
                ).build().also { instance = it }
            }
        }
    }
}



