package com.example.vetclinic.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true),
        Index(value = ["phone_number"], unique = true),
        Index(value = ["user_last_name", "user_name"])]
)

data class UserDbModel(
    @PrimaryKey

    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "user_last_name") val userLastName: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "email") val email: String
) {

}