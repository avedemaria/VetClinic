package com.example.vetclinic.data

import android.util.Log
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession


class RepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val supabaseApiService: SupabaseApiService,
    private val vetClinicDao: VetClinicDao,
    private val mapper: UserMapper
) : Repository {


    override suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return try {
            var userSession: UserSession? = null

            Email.login(
                supabaseClient,
                onSuccess = { session ->
                    userSession = session
                }
            ) {
                this.email = email
                this.password = password
            }
            userSession?.let { Result.success(it) } ?: Result.failure(Exception("Session is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun registerUser(
        email: String,
        password: String
    ): Result<UserSession> {
        return try {
            var userSession: UserSession? = null
            Email.signUp(
                supabaseClient,
                onSuccess = { session ->
                    userSession = session
                }
            ) {
                this.email = email
                this.password = password
            }
            userSession?.let { Result.success(it) } ?: Result.failure(Exception("Session is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun logOut() {
        supabaseClient.auth.signOut()
    }

    override fun getCurrentUser(): io.github.jan.supabase.auth.user.UserInfo =
        supabaseClient.auth.currentUserOrNull() ?: throw Exception("No authenticated user found")


    override suspend fun addUserToSupabaseDb(user: User) {
        val userDto = mapper.userEntityToUserDto(user)
        supabaseApiService.addUser(userDto)
    }

}





