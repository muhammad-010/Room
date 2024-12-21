package com.example.puasasunnah.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface JadwalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(jadwal: Jadwal)

    @Update
    suspend fun update(jadwal: Jadwal)

    @Delete
    fun delete(jadwal: Jadwal)

    @Query("SELECT * FROM jadwal_table WHERE id = :id LIMIT 1")
    fun getJadwal(id: Int): LiveData<Jadwal>

    @Query("SELECT * FROM jadwal_table WHERE id = :id LIMIT 1")
    fun getJadwalSync(id: Int): Jadwal?

    @Query("SELECT * FROM jadwal_table WHERE isFavorite = 1")
    fun getAllFavorites(): LiveData<List<Jadwal>>

    @get:Query("SELECT * FROM jadwal_table ORDER BY id ASC")
    val allJadwals: LiveData<List<Jadwal>>
}