package com.example.vetclinic.data.database.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface VetClinicDao {

    @Query("SELECT * FROM users WHERE uid=:userId")
    fun getUserById(userId: String): UserDbModel?

    @Query("SELECT * FROM pets WHERE user_id=:userId")
    fun getPetsByUserId(userId: String): List<PetDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(per: PetDbModel)

    @Transaction
    @Query("SELECT * FROM users WHERE uid = :userId")
    suspend fun getUserWithPets(userId: String): UserWithPetsDbModel?

}