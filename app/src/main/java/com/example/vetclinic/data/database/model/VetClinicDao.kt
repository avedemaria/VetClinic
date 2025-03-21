package com.example.vetclinic.data.database.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update


@Dao
interface VetClinicDao {

    @Query("SELECT * FROM users WHERE uid=:userId")
    fun getUserById(userId: String): UserDbModel?

    @Query("SELECT * FROM pets WHERE user_id=:userId")
    fun getPetsByUserId(userId: String): List<PetDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserDbModel)

    @Update
    suspend fun updateUser(user: UserDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPets(pets: List<PetDbModel>)

    @Query("SELECT * FROM pets")
    suspend fun getAllPets(): List<PetDbModel>

    @Update
    suspend fun updatePet(pet: PetDbModel)

    @Delete
    suspend fun deletePet(pet: PetDbModel)

    @Transaction
    @Query("SELECT * FROM users WHERE uid = :userId")
    suspend fun getUserWithPets(userId: String): UserWithPetsDbModel?

}