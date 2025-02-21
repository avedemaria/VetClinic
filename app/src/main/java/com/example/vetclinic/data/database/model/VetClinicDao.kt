package com.example.vetclinic.data.database.model

import androidx.room.Dao
import androidx.room.Query


@Dao
interface VetClinicDao {
    //getting user's info
    @Query("SELECT * FROM users WHERE uid=:userUid")
    fun getUserById(userUid: String): UserDbModel


}