package com.example.vetclinic.data.database.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface VetClinicDao {

    @Query("SELECT * FROM users WHERE uid=:userId")
    fun getUserById(userId: String): UserDbModel?

    @Query("SELECT * FROM pets WHERE user_id=:userId")
    fun getPetsByUserId(userId: String): List<PetDbModel>

    @Query("SELECT * FROM pets WHERE pet_id=:petId")
    fun getPetById(petId: String): PetDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserDbModel)

    @Update
    suspend fun updateUser(user: UserDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPets(pets: List<PetDbModel>)

    @Update
    suspend fun updatePet(pet: PetDbModel)

    @Delete
    suspend fun deletePet(pet: PetDbModel)

    @Transaction
    @Query("SELECT * FROM users WHERE uid = :userId")
    suspend fun getUserWithPets(userId: String): UserWithPetsDbModel?

    @Query("DELETE FROM users")
    suspend fun clearUserData()

    @Query("DELETE FROM pets")
    suspend fun clearAllPets()

    @Query("DELETE FROM appointments")
    suspend fun clearAllAppointments()


    @Query("SELECT * FROM appointments WHERE user_id=:userId")
    fun observeAppointmentsByUserId(userId: String): Flow<List<AppointmentWithDetailsDbModel>>


    @Query("SELECT * FROM appointments WHERE  date(date_time) = :selectedDate")
    fun observeAppointmentsPaging(
        selectedDate: String,
    ): PagingSource<Int, AppointmentWithDetailsDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentWithDetailsDbModel>)

    @Update
    suspend fun updateAppointment(appointment: AppointmentWithDetailsDbModel)

    @Transaction
    suspend fun refresh(appointments:List<AppointmentWithDetailsDbModel>) {
        clearAllAppointments()
        insertAppointments(appointments)
    }

}