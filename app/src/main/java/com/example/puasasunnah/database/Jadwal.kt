package com.example.puasasunnah.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.puasasunnah.model.JadwalCategory
import com.example.puasasunnah.model.JadwalType

@Entity(tableName = "jadwal_table")
data class Jadwal(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val id: Int,
    @ColumnInfo(name = "category_id")
    val categoryId: Int,
    @ColumnInfo(name = "type_id")
    val typeId: Int,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "month")
    val month: Int,
    @ColumnInfo(name = "day")
    val day: Int,
    @ColumnInfo(name = "human_date")
    val humanDate: String,
    @ColumnInfo(name = "category")
    val category: JadwalCategory,
    @ColumnInfo(name = "type")
    val type: JadwalType,

    var isFavorite: Boolean = false
)
