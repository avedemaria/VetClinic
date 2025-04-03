package com.example.vetclinic.domain

import com.example.vetclinic.domain.entities.Department
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.Service
import com.example.vetclinic.domain.entities.User
import io.github.jan.supabase.auth.user.UserSession

interface Repository {

    suspend fun loginUser(email: String, password: String): Result<UserSession>

    suspend fun registerUser(
        email: String,
        password: String,
    ): Result<UserSession>

    suspend fun logOut(): Result<Unit>

    suspend fun resetPasswordWithEmail(email: String): Result<Unit>

    suspend fun updatePassword(newPassword: String, token: String, email: String): Result<Unit>

    suspend fun getUserFromSupabaseDb(userId: String): Result<User?>

    suspend fun updateUserInSupabaseDb(userId: String, updatedUser: User): Result<Unit>

    suspend fun updatePetInSupabaseDb(petId: String, updatedPet: Pet): Result<Unit>

    suspend fun addUserToSupabaseDb(user: User): Result<Unit>

    suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit>

    suspend fun getPetsFromSupabaseDb(userId: String): Result<List<Pet>>

    suspend fun deletePetFromSupabaseDb(pet: Pet): Result<Unit>

    suspend fun getDoctorList(): Result<List<Doctor>>

    suspend fun getDepartmentList(): Result<List<Department>>

    suspend fun getServiceList(): Result<List<Service>>

    suspend fun getServicesByDepartmentId(departmentId: String): Result<List<Service>>

    suspend fun getServiceById(serviceId: String): Result<Service>

    suspend fun checkUserSession(): Boolean

    suspend fun addUserAndPetToRoom(user: User, pet: Pet)

    suspend fun addUserToRoom(user: User)

    suspend fun addPetToRoom(pet: Pet): Result<Unit>

    suspend fun updateUserInRoom(user: User): Result<Unit>

    suspend fun updatePetInRoom(pet: Pet): Result<Unit>

    suspend fun getCurrentUserFromRoom(userId: String): Result<User>

    suspend fun getPetsFromRoom(userId: String): Result<List<Pet>>

    suspend fun deletePetFromRoom(pet: Pet)

    suspend fun clearAllData()

//    suspend fun getPetsFromSupabaseDb(userId: String)



    //time slots and appointments


}