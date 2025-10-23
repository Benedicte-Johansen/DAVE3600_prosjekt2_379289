package com.example.dave3600_prosjekt2_379289.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dave3600_prosjekt2_379289.domain.Friend

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val birthDate: String,
    val phone: String
)

fun FriendEntity.toDomainModel(): Friend {
    return Friend(
        id = this.id,
        name = this.name,
        birthDate = this.birthDate,
        phone = this.phone
    )
}

fun Friend.toEntity(): FriendEntity {
    return FriendEntity(
        id = this.id,
        name = this.name,
        birthDate = this.birthDate,
        phone = this.phone
    )
}