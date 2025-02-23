package com.example.vetclinic.data.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pets",
    foreignKeys = [ForeignKey(
        entity = UserDbModel::class,
        parentColumns = ["uid"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"])]
)
data class PetDbModel(
    @PrimaryKey val petId: String,
    val userId: String,
    val petName: String,
    val petType: String? = null,
    val petAge: String? = null
)