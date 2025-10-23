package com.example.dave3600_prosjekt2_379289.data

import com.example.dave3600_prosjekt2_379289.domain.Friend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FriendRepository(private val friendDao: FriendDao) {

    /**
     * Henter alle venner som en Flow (oppdateres automatisk)
     */
    fun getAllFriendsStream(): Flow<List<Friend>> {
        return friendDao.getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Henter en enkelt venn basert på ID
     * @return Friend hvis funnet, null hvis ikke funnet
     */
    suspend fun getFriendById(id: Long): Friend? {
        return friendDao.getById(id)?.toDomainModel()
    }

    /**
     * Legger til en ny venn i databasen
     */
    suspend fun insertFriend(friend: Friend) {
        friendDao.insert(friend.toEntity())
    }

    /**
     * Oppdaterer en eksisterende venn
     */
    suspend fun updateFriend(friend: Friend) {
        friendDao.update(friend.toEntity())
    }

    /**
     * Sletter en venn fra databasen
     */
    suspend fun deleteFriend(friend: Friend) {
        friendDao.delete(friend.toEntity())
    }

    /**
     * Henter alle venner som en liste (brukes av WorkManager)
     * Dette er en "snapshot" i motsetning til Flow som er live-oppdatert
     */
    suspend fun getAllFriendsList(): List<Friend> {
        return friendDao.getAllFriendsList().map { it.toDomainModel() }
    }
}