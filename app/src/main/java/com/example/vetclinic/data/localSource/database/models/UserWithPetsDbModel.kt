package com.example.vetclinic.data.localSource.database.models

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithPetsDbModel(
    @Embedded
    val user: UserDbModel,
    @Relation(
        parentColumn = "uid",
        entityColumn = "user_id"
    )
    val pets: List<PetDbModel>
)
