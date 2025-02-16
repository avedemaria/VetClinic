package com.example.vetclinic.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserDbModel(
    @PrimaryKey
    val uid: String,
    val userName: String,
    val userLastName: String,
    val petName: String,
    val phoneNumber: String,
    val email: String
) {

}