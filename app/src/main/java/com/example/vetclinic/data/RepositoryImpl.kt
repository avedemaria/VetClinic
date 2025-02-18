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
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject
import kotlinx.coroutines.CompletableDeferred
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class RepositoryImpl @Inject constructor(
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


//    override suspend fun registerUser(
//        email: String,
//        password: String
//    ): Result<UserSession> {
//        return try {
//            val userSession = suspendCoroutine { continuation ->
//                Email.signUp(
//                    supabaseClient,
//                    onSuccess = { session ->
//                        continuation.resume(session)
//                    }
//                ) {
//                    this.email = email
//                    this.password = password
//                }
//            }
//            Result.success(userSession)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    override suspend fun registerUser(email: String, password: String): Result<UserSession> {
        return try {
            // Create a deferred value to hold our result
            val deferred = CompletableDeferred<UserSession>()

            // Call signUp with the deferred
            Email.signUp(
                supabaseClient,
                onSuccess = { session ->
                    deferred.complete(session)
                }
            ) {
                this.email = email
                this.password = password
            }

            // Wait for and return the result
            Result.success(deferred.await())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun logOut() {
        supabaseClient.auth.signOut()
    }

    override fun getCurrentUser(): io.github.jan.supabase.auth.user.UserInfo =
        supabaseClient.auth.currentUserOrNull() ?: throw Exception("No authenticated user found")

//    override suspend fun addUserToSupabaseDb(user: User) {
//
//        withContext(Dispatchers.IO) {
//            supabaseClient.from("users").insert(user)
//        }
//    }


    override suspend fun addUserToSupabaseDb(user: User): Result<Unit> {

        return try {
            Log.d("UserRepository", "Starting to add user: ${user.uid}")
            val userDto = mapper.userEntityToUserDto(user)
            Log.d("UserRepository", "Mapped to DTO: $userDto")
            val response = supabaseApiService.addUser(userDto)

            if (response.isSuccessful) {
                Log.d("UserRepository", "Successfully added user to Supabase DB")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UserRepository", "Failed to add user. Error: $errorBody")
                Result.failure(Exception("Failed to add user: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception while adding user", e)
            Result.failure(e)
        }
    }
}







