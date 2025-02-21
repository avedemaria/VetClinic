package com.example.vetclinic.data.database.model

import androidx.room.Dao
import androidx.room.Query
import com.example.vetclinic.CodeReview

@Dao
interface VetClinicDao {
    @CodeReview("Комментарий не нужен, метод с хорошим названием")
    //getting user's info
    @Query("SELECT * FROM users WHERE uid=:userUid")
    fun getUserById(userUid: String): UserDbModel // Метод возвращает UserDbModel, а что будет, если в БД нет юзера?
}