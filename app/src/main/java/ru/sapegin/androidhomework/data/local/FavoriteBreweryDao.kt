package ru.sapegin.androidhomework.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBreweryDao {

    @Query(
        "SELECT * FROM favorite_breweries " +
                "ORDER BY name COLLATE NOCASE ASC"
    )
    fun observeAll(): Flow<List<FavoriteBreweryEntity>>

    @Query("SELECT id FROM favorite_breweries")
    fun observeIds(): Flow<List<String>>

    @Query(
        "SELECT * FROM favorite_breweries WHERE id = :id LIMIT 1"
    )
    suspend fun findById(id: String): FavoriteBreweryEntity?

    @Query(
        "SELECT EXISTS(" +
                "SELECT 1 FROM favorite_breweries WHERE id = :id" +
                ")"
    )
    suspend fun isFavorite(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteBreweryEntity)

    @Query("DELETE FROM favorite_breweries WHERE id = :id")
    suspend fun deleteById(id: String)
}