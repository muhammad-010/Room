package com.example.puasasunnah.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Jadwal::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class JadwalDatabase: RoomDatabase() {
    abstract fun jadwalDao(): JadwalDao?
    companion object {
        @Volatile
        private var INSTANCE: JadwalDatabase? = null
        fun getDatabase(context: Context): JadwalDatabase? {
            if(INSTANCE == null){
                synchronized(JadwalDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        JadwalDatabase::class.java,
                        "jadwal_database"
                    )
                        .build()
                }
            }

            return INSTANCE
        }
    }
}