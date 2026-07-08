package com.savedplaces.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Place::class], version = 1, exportSchema = false)
abstract class PlaceDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: PlaceDatabase? = null

        fun getInstance(context: Context): PlaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaceDatabase::class.java,
                    "places_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
