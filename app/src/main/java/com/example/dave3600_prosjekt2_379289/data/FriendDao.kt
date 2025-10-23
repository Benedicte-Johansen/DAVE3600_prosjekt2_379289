package com.example.dave3600_prosjekt2_379289.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {

    /**
     * Henter alle venner sortert etter navn som en Flow
     * Flow oppdateres automatisk når data endres i databasen
     */
    @Query("SELECT * FROM friends ORDER BY name ASC")
    fun getAll(): Flow<List<FriendEntity>>

    /**
     * Henter en enkelt venn basert på ID
     * @param id ID-en til vennen
     * @return FriendEntity hvis funnet, null hvis ikke
     */
    @Query("SELECT * FROM friends WHERE id = :id")
    suspend fun getById(id: Long): FriendEntity?

    /**
     * Legger til en ny venn
     * @param friend Vennen som skal legges til
     */
    @Insert
    suspend fun insert(friend: FriendEntity)

    /**
     * Oppdaterer en eksisterende venn
     * @param friend Vennen som skal oppdateres
     */
    @Update
    suspend fun update(friend: FriendEntity)

    /**
     * Sletter en venn
     * @param friend Vennen som skal slettes
     */
    @Delete
    suspend fun delete(friend: FriendEntity)

    /**
     * Henter alle venner som en liste (ikke Flow)
     * Brukes av WorkManager for å sjekke bursdager
     * @return Liste med alle venner
     */
    @Query("SELECT * FROM friends")
    suspend fun getAllFriendsList(): List<FriendEntity>
}
