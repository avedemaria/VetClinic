package com.example.vetclinic.domain.interfaces

import com.example.vetclinic.domain.entities.department.Department
import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.service.Service
import com.example.vetclinic.domain.entities.user.User
import io.github.jan.supabase.auth.user.UserSession

interface Repository {

    suspend fun loginUser(email: String, password: String): Result<UserSession>

    suspend fun registerUser(
        email: String,
        password: String,
    ): Result<UserSession>

    suspend fun logOut(): Result<Unit>

    suspend fun resetPasswordWithEmail(email: String): Result<Unit>

    suspend fun updatePassword(newPassword: String, token: String): Result<Unit>

    suspend fun deleteUserAccount (): Result<Unit>

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