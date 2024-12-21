package com.example.puasasunnah.database

import androidx.room.TypeConverter
import com.example.puasasunnah.model.JadwalCategory
import com.example.puasasunnah.model.JadwalType
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromJadwalCategory(value: JadwalCategory): String {
        return value.id.toString()
    }

    @TypeConverter
    fun toJadwalCategory(value: String): JadwalCategory {
        return JadwalCategory(id = value.toInt(), name = "")
    }

    @TypeConverter
    fun fromJadwalType(value: JadwalType): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toJadwalType(value: String): JadwalType {
        val gson = Gson()
        return gson.fromJson(value, JadwalType::class.java)
    }
}
