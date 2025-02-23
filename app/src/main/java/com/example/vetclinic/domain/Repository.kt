package com.example.vetclinic.domain

import com.example.vetclinic.data.database.model.UserDbModel
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.User
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession

interface Repository {

    suspend fun loginUser(email: String, password: String): Result<UserSession>

    suspend fun registerUser(
        email: String,
        password: String,
    ): Result<UserSession>

    suspend fun logOut()

    fun getCurrentUser(): Result<UserInfo>


    suspend fun addUserToSupabaseDb(user: User): Result<Unit>

    suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit>

    suspend fun getDoctorList(): Result<List<Doctor>>

    suspend fun checkUserSession(): Boolean

    suspend fun addUserToRoom(user: User, pet: Pet)

    suspend fun getCurrentUserFromRoom (userId: String): Result<User>
}