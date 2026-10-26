package ru.sapegin.androidhomework.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteBreweryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BreweryDatabase : RoomDatabase() {

    abstract fun favoriteBreweryDao(): FavoriteBreweryDao

    companion object {

        @Volatile
        private var INSTANCE: BreweryDatabase? = null

        fun getInstance(context: Context): BreweryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BreweryDatabase::class.java,
                    "brewery_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}